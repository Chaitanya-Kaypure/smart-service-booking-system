package com.smartbooking.mapper;

import com.smartbooking.dto.service.ServiceResponse;
import com.smartbooking.dto.service.ServiceSummaryResponse;
import com.smartbooking.entity.ServiceEntity;
import org.springframework.stereotype.Component;

@Component
public class ServiceMapper {

    public ServiceResponse toResponse(ServiceEntity service) {
        return ServiceResponse.builder()
                .id(service.getId())
                .name(service.getName())
                .description(service.getDescription())
                .category(service.getCategory())
                .price(service.getPrice())
                .durationMinutes(service.getDurationMinutes())
                .active(service.isActive())
                .createdAt(service.getCreatedAt())
                .updatedAt(service.getUpdatedAt())
                .build();
    }

    public ServiceSummaryResponse toSummary(ServiceEntity service) {
        return ServiceSummaryResponse.builder()
                .id(service.getId())
                .name(service.getName())
                .category(service.getCategory())
                .price(service.getPrice())
                .durationMinutes(service.getDurationMinutes())
                .build();
    }
}
