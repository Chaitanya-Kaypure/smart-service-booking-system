package com.smartbooking.service;

import com.smartbooking.dto.appointment.AppointmentResponse;
import com.smartbooking.dto.queue.MyQueueStatusResponse;
import com.smartbooking.dto.queue.QueueSnapshotResponse;
import com.smartbooking.entity.Appointment;
import com.smartbooking.entity.AppointmentStatus;
import com.smartbooking.entity.QueueSession;
import com.smartbooking.entity.ServiceEntity;
import com.smartbooking.exception.BadRequestException;
import com.smartbooking.exception.ResourceNotFoundException;
import com.smartbooking.mapper.AppointmentMapper;
import com.smartbooking.repository.AppointmentRepository;
import com.smartbooking.repository.QueueSessionRepository;
import com.smartbooking.security.SecurityUtils;
import com.smartbooking.validation.AppointmentStatusTransitionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QueueService {

    private final AppointmentRepository appointmentRepository;
    private final QueueSessionRepository queueSessionRepository;
    private final ServiceCatalogService serviceCatalogService;
    private final AppointmentMapper appointmentMapper;

    @Transactional(readOnly = true)
    public QueueSnapshotResponse getQueueSnapshot(Long serviceId, LocalDate date) {
        ServiceEntity service = serviceCatalogService.getServiceEntity(serviceId);
        QueueSession session = queueSessionRepository.findByServiceIdAndQueueDate(serviceId, date).orElse(null);

        List<Appointment> waiting = appointmentRepository.findQueueAppointments(
                serviceId, date, AppointmentConstants.QUEUE_ACTIVE_STATUSES);

        waiting.sort(Comparator.comparingInt(Appointment::getQueueSequence));

        Optional<Appointment> currentlyServing = waiting.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.IN_PROGRESS
                        || a.getStatus() == AppointmentStatus.CHECKED_IN)
                .min(Comparator.comparingInt(Appointment::getQueueSequence));

        int currentSequence = session != null ? session.getCurrentServingSequence() : 0;
        String currentToken = currentlyServing.map(Appointment::getQueueToken).orElse(null);

        int avgDuration = service.getDurationMinutes();
        int estimatedWait = waiting.size() * avgDuration;

        List<AppointmentResponse> waitingResponses = waiting.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.CONFIRMED)
                .map(appointmentMapper::toResponse)
                .toList();

        return QueueSnapshotResponse.builder()
                .serviceId(serviceId)
                .serviceName(service.getName())
                .queueDate(date)
                .currentServingSequence(currentSequence > 0 ? currentSequence : null)
                .currentServingToken(currentToken)
                .waitingAppointments(waitingResponses)
                .estimatedWaitMinutes(estimatedWait)
                .build();
    }

    @Transactional(readOnly = true)
    public MyQueueStatusResponse getMyQueueStatus() {
        Long userId = SecurityUtils.currentUser().getId();
        Appointment appointment = appointmentRepository
                .findFirstByCustomerIdAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAscStartTimeAsc(
                        userId,
                        LocalDate.now(),
                        AppointmentConstants.QUEUE_ACTIVE_STATUSES)
                .orElseThrow(() -> new ResourceNotFoundException("No active queue appointment found"));

        QueueSession session = queueSessionRepository
                .findByServiceIdAndQueueDate(appointment.getService().getId(), appointment.getAppointmentDate())
                .orElse(null);

        int currentSequence = session != null ? session.getCurrentServingSequence() : 0;
        int tokensAhead = Math.max(0, appointment.getQueueSequence() - currentSequence - 1);
        int estimatedWait = tokensAhead * appointment.getService().getDurationMinutes();

        String currentToken = null;
        if (currentSequence > 0) {
            currentToken = appointmentRepository.findQueueAppointments(
                            appointment.getService().getId(),
                            appointment.getAppointmentDate(),
                            AppointmentConstants.QUEUE_ACTIVE_STATUSES)
                    .stream()
                    .filter(a -> a.getQueueSequence() == currentSequence)
                    .map(Appointment::getQueueToken)
                    .findFirst()
                    .orElse(null);
        }

        return MyQueueStatusResponse.builder()
                .appointmentId(appointment.getId())
                .serviceId(appointment.getService().getId())
                .serviceName(appointment.getService().getName())
                .appointmentDate(appointment.getAppointmentDate())
                .startTime(appointment.getStartTime())
                .queueToken(appointment.getQueueToken())
                .queueSequence(appointment.getQueueSequence())
                .currentServingSequence(currentSequence > 0 ? currentSequence : null)
                .currentServingToken(currentToken)
                .tokensAhead(tokensAhead)
                .estimatedWaitMinutes(estimatedWait)
                .status(appointment.getStatus())
                .build();
    }

    @Transactional
    public AppointmentResponse callNext(Long serviceId, LocalDate date) {
        QueueSession session = queueSessionRepository.findForUpdate(serviceId, date)
                .orElseThrow(() -> new BadRequestException("No queue session exists for the selected date"));

        List<Appointment> queue = appointmentRepository.findQueueAppointments(
                serviceId, date, AppointmentConstants.QUEUE_ACTIVE_STATUSES);
        queue.sort(Comparator.comparingInt(Appointment::getQueueSequence));

        Appointment next = queue.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.CONFIRMED)
                .findFirst()
                .orElseThrow(() -> new BadRequestException("No waiting customers in queue"));

        AppointmentStatusTransitionValidator.validateTransition(next.getStatus(), AppointmentStatus.CHECKED_IN);
        next.setStatus(AppointmentStatus.CHECKED_IN);
        AppointmentStatusTransitionValidator.validateTransition(AppointmentStatus.CHECKED_IN, AppointmentStatus.IN_PROGRESS);
        next.setStatus(AppointmentStatus.IN_PROGRESS);

        session.setCurrentServingSequence(next.getQueueSequence());
        queueSessionRepository.save(session);
        return appointmentMapper.toResponse(appointmentRepository.save(next));
    }

    @Transactional
    public AppointmentResponse completeAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        if (appointment.getStatus() != AppointmentStatus.IN_PROGRESS
                && appointment.getStatus() != AppointmentStatus.CHECKED_IN) {
            throw new BadRequestException("Only in-progress appointments can be completed");
        }
        AppointmentStatusTransitionValidator.validateTransition(appointment.getStatus(), AppointmentStatus.COMPLETED);
        appointment.setStatus(AppointmentStatus.COMPLETED);
        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Transactional
    public AppointmentResponse markNoShow(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        AppointmentStatusTransitionValidator.validateTransition(appointment.getStatus(), AppointmentStatus.NO_SHOW);
        appointment.setStatus(AppointmentStatus.NO_SHOW);
        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }
}
