package com.smartbooking.mapper;

import com.smartbooking.dto.appointment.AppointmentResponse;
import com.smartbooking.entity.Appointment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentMapper {

    private final UserMapper userMapper;
    private final ServiceMapper serviceMapper;

    public AppointmentResponse toResponse(Appointment appointment) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .bookingReference(appointment.getBookingReference())
                .customer(userMapper.toSummary(appointment.getCustomer()))
                .service(serviceMapper.toSummary(appointment.getService()))
                .appointmentDate(appointment.getAppointmentDate())
                .startTime(appointment.getStartTime())
                .endTime(appointment.getEndTime())
                .queueToken(appointment.getQueueToken())
                .queueSequence(appointment.getQueueSequence())
                .status(appointment.getStatus())
                .notes(appointment.getNotes())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .build();
    }
}
