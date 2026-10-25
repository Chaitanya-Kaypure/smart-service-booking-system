package com.smartbooking.dto.queue;

import com.smartbooking.dto.appointment.AppointmentResponse;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class QueueSnapshotResponse {

    private Long serviceId;
    private String serviceName;
    private LocalDate queueDate;
    private Integer currentServingSequence;
    private String currentServingToken;
    private List<AppointmentResponse> waitingAppointments;
    private int estimatedWaitMinutes;
}
