package com.smartbooking.dto.service;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
public class AvailabilitySlotResponse {

    private LocalTime startTime;
    private LocalTime endTime;
    private boolean available;
}
