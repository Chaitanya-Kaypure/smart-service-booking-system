package com.smartbooking.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class ReportsResponse {

    private Map<String, Long> appointmentsByStatus;
    private List<ChartPoint> bookingTrends;
    private List<ServiceDistributionPoint> bookingsByService;
}
