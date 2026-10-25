package com.smartbooking.service;

import com.smartbooking.dto.admin.ChartPoint;
import com.smartbooking.dto.admin.DashboardResponse;
import com.smartbooking.dto.admin.ReportsResponse;
import com.smartbooking.dto.admin.ServiceDistributionPoint;
import com.smartbooking.entity.AppointmentStatus;
import com.smartbooking.mapper.AppointmentMapper;
import com.smartbooking.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        LocalDate today = LocalDate.now();
        LocalDate trendStart = today.minusDays(13);

        long waiting = appointmentRepository.countByAppointmentDateAndStatus(today, AppointmentStatus.CONFIRMED)
                + appointmentRepository.countByAppointmentDateAndStatus(today, AppointmentStatus.CHECKED_IN);

        List<ChartPoint> trends = appointmentRepository.countBookingsByDateSince(trendStart).stream()
                .map(row -> ChartPoint.builder()
                        .date((LocalDate) row[0])
                        .count((Long) row[1])
                        .build())
                .toList();

        List<ServiceDistributionPoint> byService = appointmentRepository.countBookingsByServiceSince(trendStart).stream()
                .map(row -> ServiceDistributionPoint.builder()
                        .serviceName((String) row[0])
                        .count((Long) row[1])
                        .build())
                .toList();

        int avgWait = (int) Math.round(byService.stream().mapToLong(ServiceDistributionPoint::getCount).average().orElse(0));

        return DashboardResponse.builder()
                .todaysBookings(appointmentRepository.countByAppointmentDateAndStatus(today, AppointmentStatus.CONFIRMED)
                        + appointmentRepository.countByAppointmentDateAndStatus(today, AppointmentStatus.CHECKED_IN)
                        + appointmentRepository.countByAppointmentDateAndStatus(today, AppointmentStatus.IN_PROGRESS)
                        + appointmentRepository.countByAppointmentDateAndStatus(today, AppointmentStatus.COMPLETED))
                .upcomingAppointments(appointmentRepository.countByStatus(AppointmentStatus.CONFIRMED))
                .completedAppointments(appointmentRepository.countByStatus(AppointmentStatus.COMPLETED))
                .cancelledAppointments(appointmentRepository.countByStatus(AppointmentStatus.CANCELLED))
                .waitingCustomers(waiting)
                .averageEstimatedWaitMinutes(avgWait)
                .bookingTrends(trends)
                .bookingsByService(byService)
                .recentBookings(appointmentRepository.findTop10ByOrderByCreatedAtDesc().stream()
                        .map(appointmentMapper::toResponse)
                        .toList())
                .build();
    }

    @Transactional(readOnly = true)
    public ReportsResponse getReports() {
        LocalDate trendStart = LocalDate.now().minusDays(29);
        Map<String, Long> byStatus = new LinkedHashMap<>();
        Arrays.stream(AppointmentStatus.values()).forEach(status ->
                byStatus.put(status.name(), appointmentRepository.countByStatus(status)));

        return ReportsResponse.builder()
                .appointmentsByStatus(byStatus)
                .bookingTrends(appointmentRepository.countBookingsByDateSince(trendStart).stream()
                        .map(row -> ChartPoint.builder()
                                .date((LocalDate) row[0])
                                .count((Long) row[1])
                                .build())
                        .toList())
                .bookingsByService(appointmentRepository.countBookingsByServiceSince(trendStart).stream()
                        .map(row -> ServiceDistributionPoint.builder()
                                .serviceName((String) row[0])
                                .count((Long) row[1])
                                .build())
                        .toList())
                .build();
    }
}
