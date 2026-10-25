package com.smartbooking.dto.service;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class ServiceResponse {

    private Long id;
    private String name;
    private String description;
    private String category;
    private BigDecimal price;
    private int durationMinutes;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}
