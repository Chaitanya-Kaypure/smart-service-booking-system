package com.smartbooking.controller;

import com.smartbooking.dto.appointment.AppointmentResponse;
import com.smartbooking.dto.appointment.AppointmentStatusUpdateRequest;
import com.smartbooking.dto.common.ApiResponse;
import com.smartbooking.entity.AppointmentStatus;
import com.smartbooking.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/appointments")
@RequiredArgsConstructor
public class AdminAppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping
    public ApiResponse<Page<AppointmentResponse>> search(
            @RequestParam(required = false) Long serviceId,
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String customer,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.ok(appointmentService.adminSearch(serviceId, status, date, customer, pageable));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<AppointmentResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentStatusUpdateRequest request) {
        return ApiResponse.ok(appointmentService.updateStatus(id, request));
    }
}
