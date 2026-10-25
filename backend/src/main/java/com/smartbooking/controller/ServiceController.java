package com.smartbooking.controller;

import com.smartbooking.dto.common.ApiResponse;
import com.smartbooking.dto.service.DailyAvailabilityResponse;
import com.smartbooking.dto.service.ServiceResponse;
import com.smartbooking.service.AvailabilityService;
import com.smartbooking.service.ServiceCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceCatalogService serviceCatalogService;
    private final AvailabilityService availabilityService;

    @GetMapping
    public ApiResponse<Page<ServiceResponse>> listServices(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.ok(serviceCatalogService.listPublicServices(search, category, pageable));
    }

    @GetMapping("/categories")
    public ApiResponse<List<String>> categories() {
        return ApiResponse.ok(serviceCatalogService.listCategories());
    }

    @GetMapping("/{id}")
    public ApiResponse<ServiceResponse> getService(@PathVariable Long id) {
        return ApiResponse.ok(serviceCatalogService.getActiveService(id));
    }

    @GetMapping("/{id}/availability")
    public ApiResponse<DailyAvailabilityResponse> availability(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.ok(availabilityService.getDailyAvailability(id, date));
    }
}
