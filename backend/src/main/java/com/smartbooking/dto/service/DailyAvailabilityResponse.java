package com.smartbooking.dto.service;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class DailyAvailabilityResponse {

    private Long serviceId;
    private LocalDate date;
    private int slotIntervalMinutes;
    private List<AvailabilitySlotResponse> slots;
}
