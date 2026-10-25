package com.smartbooking.dto.admin;

import com.smartbooking.dto.appointment.AppointmentResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class DashboardResponse {

    private long todaysBookings;
    private long upcomingAppointments;
    private long completedAppointments;
    private long cancelledAppointments;
    private long waitingCustomers;
    private int averageEstimatedWaitMinutes;
    private List<ChartPoint> bookingTrends;
    private List<ServiceDistributionPoint> bookingsByService;
    private List<AppointmentResponse> recentBookings;
}
