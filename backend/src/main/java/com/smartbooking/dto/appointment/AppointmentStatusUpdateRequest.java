package com.smartbooking.dto.appointment;

import com.smartbooking.entity.AppointmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AppointmentStatusUpdateRequest {

    @NotNull
    private AppointmentStatus status;
}
