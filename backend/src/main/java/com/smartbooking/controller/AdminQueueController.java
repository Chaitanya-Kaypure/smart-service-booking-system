package com.smartbooking.controller;

import com.smartbooking.dto.appointment.AppointmentResponse;
import com.smartbooking.dto.common.ApiResponse;
import com.smartbooking.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/queue")
@RequiredArgsConstructor
public class AdminQueueController {

    private final QueueService queueService;

    @PostMapping("/{serviceId}/next")
    public ApiResponse<AppointmentResponse> callNext(
            @PathVariable Long serviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.ok(queueService.callNext(serviceId, date));
    }

    @PostMapping("/appointments/{appointmentId}/complete")
    public ApiResponse<AppointmentResponse> complete(@PathVariable Long appointmentId) {
        return ApiResponse.ok(queueService.completeAppointment(appointmentId));
    }

    @PostMapping("/appointments/{appointmentId}/no-show")
    public ApiResponse<AppointmentResponse> noShow(@PathVariable Long appointmentId) {
        return ApiResponse.ok(queueService.markNoShow(appointmentId));
    }
}
