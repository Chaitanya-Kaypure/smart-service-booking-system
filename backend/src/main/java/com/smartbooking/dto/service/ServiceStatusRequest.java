package com.smartbooking.dto.service;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ServiceStatusRequest {

    @NotNull
    private Boolean active;
}
