package com.smartbooking.controller;

import com.smartbooking.dto.common.ApiResponse;
import com.smartbooking.dto.service.ServiceCreateRequest;
import com.smartbooking.dto.service.ServiceResponse;
import com.smartbooking.dto.service.ServiceStatusRequest;
import com.smartbooking.dto.service.ServiceUpdateRequest;
import com.smartbooking.service.ServiceCatalogService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/services")
@RequiredArgsConstructor
public class AdminServiceController {

    private final ServiceCatalogService serviceCatalogService;

    @GetMapping
    public ApiResponse<Page<ServiceResponse>> listAll(@PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.ok(serviceCatalogService.listAllServices(pageable));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ServiceResponse> create(@Valid @RequestBody ServiceCreateRequest request) {
        return ApiResponse.ok(serviceCatalogService.createService(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<ServiceResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ServiceUpdateRequest request) {
        return ApiResponse.ok(serviceCatalogService.updateService(id, request));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<ServiceResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody ServiceStatusRequest request) {
        return ApiResponse.ok(serviceCatalogService.updateStatus(id, request));
    }
}
