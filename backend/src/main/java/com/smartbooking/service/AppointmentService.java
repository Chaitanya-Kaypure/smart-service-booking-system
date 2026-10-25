package com.smartbooking.service;

import com.smartbooking.dto.appointment.AppointmentCreateRequest;
import com.smartbooking.dto.appointment.AppointmentResponse;
import com.smartbooking.dto.appointment.AppointmentStatusUpdateRequest;
import com.smartbooking.entity.Appointment;
import com.smartbooking.entity.AppointmentStatus;
import com.smartbooking.entity.QueueSession;
import com.smartbooking.entity.ServiceEntity;
import com.smartbooking.entity.User;
import com.smartbooking.exception.BadRequestException;
import com.smartbooking.exception.ConflictException;
import com.smartbooking.exception.ForbiddenException;
import com.smartbooking.exception.ResourceNotFoundException;
import com.smartbooking.mapper.AppointmentMapper;
import com.smartbooking.repository.AppointmentRepository;
import com.smartbooking.repository.QueueSessionRepository;
import com.smartbooking.security.SecurityUtils;
import com.smartbooking.security.UserPrincipal;
import com.smartbooking.validation.AppointmentStatusTransitionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final QueueSessionRepository queueSessionRepository;
    private final ServiceCatalogService serviceCatalogService;
    private final UserService userService;
    private final AvailabilityService availabilityService;
    private final AppointmentMapper appointmentMapper;

    @Transactional
    public AppointmentResponse createAppointment(AppointmentCreateRequest request) {
        UserPrincipal principal = SecurityUtils.currentUser();
        User customer = userService.getUserEntity(principal.getId());

        ServiceEntity service = serviceCatalogService.getServiceEntity(request.getServiceId());
        if (!service.isActive()) {
            throw new BadRequestException("Service is not active");
        }

        LocalDate date = request.getAppointmentDate();
        LocalTime startTime = request.getStartTime();
        LocalTime endTime = startTime.plusMinutes(service.getDurationMinutes());

        validateSlotAvailable(request.getServiceId(), date, startTime, endTime);

        if (appointmentRepository.existsCustomerSlotBooking(
                customer.getId(),
                service.getId(),
                date,
                startTime,
                AppointmentConstants.NON_BLOCKING_STATUSES)) {
            throw new ConflictException("You already have a booking for this time slot");
        }

        if (appointmentRepository.existsOverlappingBooking(
                service.getId(),
                date,
                startTime,
                endTime,
                AppointmentConstants.NON_BLOCKING_STATUSES)) {
            throw new ConflictException("Selected time slot is no longer available");
        }

        TokenAllocation allocation = allocateQueueToken(service.getId(), date);

        Appointment appointment = Appointment.builder()
                .bookingReference(UUID.randomUUID().toString())
                .customer(customer)
                .service(service)
                .appointmentDate(date)
                .startTime(startTime)
                .endTime(endTime)
                .queueToken(allocation.token())
                .queueSequence(allocation.sequence())
                .status(AppointmentStatus.CONFIRMED)
                .notes(request.getNotes())
                .build();

        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponse> getMyAppointments(Pageable pageable) {
        Long userId = SecurityUtils.currentUser().getId();
        return appointmentRepository.findByCustomerIdOrderByAppointmentDateDescStartTimeDesc(userId, pageable)
                .map(appointmentMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public AppointmentResponse getAppointment(Long id) {
        Appointment appointment = getAppointmentEntity(id);
        assertCanAccess(appointment);
        return appointmentMapper.toResponse(appointment);
    }

    @Transactional
    public AppointmentResponse cancelAppointment(Long id) {
        Appointment appointment = getAppointmentEntity(id);
        assertCanAccess(appointment);
        if (AppointmentStatusTransitionValidator.isTerminal(appointment.getStatus())) {
            throw new BadRequestException("Appointment cannot be cancelled");
        }
        AppointmentStatusTransitionValidator.validateTransition(appointment.getStatus(), AppointmentStatus.CANCELLED);
        appointment.setStatus(AppointmentStatus.CANCELLED);
        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponse> adminSearch(
            Long serviceId,
            AppointmentStatus status,
            LocalDate date,
            String customerSearch,
            Pageable pageable) {
        return appointmentRepository.adminSearch(serviceId, status, date, customerSearch, pageable)
                .map(appointmentMapper::toResponse);
    }

    @Transactional
    public AppointmentResponse updateStatus(Long id, AppointmentStatusUpdateRequest request) {
        Appointment appointment = getAppointmentEntity(id);
        AppointmentStatusTransitionValidator.validateTransition(appointment.getStatus(), request.getStatus());
        appointment.setStatus(request.getStatus());
        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    Appointment getAppointmentEntity(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
    }

    private void validateSlotAvailable(Long serviceId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        var availability = availabilityService.getDailyAvailability(serviceId, date);
        boolean match = availability.getSlots().stream()
                .anyMatch(slot -> slot.getStartTime().equals(startTime)
                        && slot.getEndTime().equals(endTime)
                        && slot.isAvailable());
        if (!match) {
            throw new BadRequestException("Selected time slot is unavailable");
        }
    }

    private TokenAllocation allocateQueueToken(Long serviceId, LocalDate date) {
        QueueSession session = queueSessionRepository.findForUpdate(serviceId, date)
                .orElseGet(() -> queueSessionRepository.save(QueueSession.builder()
                        .service(serviceCatalogService.getServiceEntity(serviceId))
                        .queueDate(date)
                        .currentServingSequence(0)
                        .nextTokenSequence(1)
                        .build()));

        int sequence = session.getNextTokenSequence();
        String token = String.format("T%03d", sequence);
        session.setNextTokenSequence(sequence + 1);
        queueSessionRepository.save(session);
        return new TokenAllocation(sequence, token);
    }

    private void assertCanAccess(Appointment appointment) {
        UserPrincipal principal = SecurityUtils.currentUser();
        boolean isOwner = appointment.getCustomer().getId().equals(principal.getId());
        boolean isAdmin = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isOwner && !isAdmin) {
            throw new ForbiddenException("You do not have access to this appointment");
        }
    }

    private record TokenAllocation(int sequence, String token) {
    }
}
