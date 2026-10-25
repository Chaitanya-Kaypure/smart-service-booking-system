package com.smartbooking.controller;

import com.smartbooking.dto.admin.DashboardResponse;
import com.smartbooking.dto.admin.ReportsResponse;
import com.smartbooking.dto.common.ApiResponse;
import com.smartbooking.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/dashboard")
    public ApiResponse<DashboardResponse> dashboard() {
        return ApiResponse.ok(adminDashboardService.getDashboard());
    }

    @GetMapping("/reports")
    public ApiResponse<ReportsResponse> reports() {
        return ApiResponse.ok(adminDashboardService.getReports());
    }
}
