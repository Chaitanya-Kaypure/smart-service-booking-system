package com.smartbooking.dto.appointment;

import com.smartbooking.dto.service.ServiceSummaryResponse;
import com.smartbooking.dto.user.UserSummaryResponse;
import com.smartbooking.entity.AppointmentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class AppointmentResponse {

    private Long id;
    private String bookingReference;
    private UserSummaryResponse customer;
    private ServiceSummaryResponse service;
    private LocalDate appointmentDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String queueToken;
    private int queueSequence;
    private AppointmentStatus status;
    private String notes;
    private Instant createdAt;
    private Instant updatedAt;
}
