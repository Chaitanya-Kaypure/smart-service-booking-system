package com.smartbooking.dto.queue;

import com.smartbooking.entity.AppointmentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class MyQueueStatusResponse {

    private Long appointmentId;
    private Long serviceId;
    private String serviceName;
    private LocalDate appointmentDate;
    private LocalTime startTime;
    private String queueToken;
    private int queueSequence;
    private Integer currentServingSequence;
    private String currentServingToken;
    private int tokensAhead;
    private int estimatedWaitMinutes;
    private AppointmentStatus status;
}
