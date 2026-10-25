package com.smartbooking.dto.service;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ServiceSummaryResponse {

    private Long id;
    private String name;
    private String category;
    private BigDecimal price;
    private int durationMinutes;
}
