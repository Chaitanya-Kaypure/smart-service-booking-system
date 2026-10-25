package com.smartbooking.controller;

import com.smartbooking.dto.appointment.AppointmentCreateRequest;
import com.smartbooking.dto.appointment.AppointmentResponse;
import com.smartbooking.dto.common.ApiResponse;
import com.smartbooking.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AppointmentResponse> create(@Valid @RequestBody AppointmentCreateRequest request) {
        return ApiResponse.ok("Appointment booked", appointmentService.createAppointment(request));
    }

    @GetMapping("/my")
    public ApiResponse<Page<AppointmentResponse>> myAppointments(@PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.ok(appointmentService.getMyAppointments(pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<AppointmentResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok(appointmentService.getAppointment(id));
    }

    @PatchMapping("/{id}/cancel")
    public ApiResponse<AppointmentResponse> cancel(@PathVariable Long id) {
        return ApiResponse.ok(appointmentService.cancelAppointment(id));
    }
}
