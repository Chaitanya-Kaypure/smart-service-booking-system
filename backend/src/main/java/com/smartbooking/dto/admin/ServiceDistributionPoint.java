package com.smartbooking.dto.admin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServiceDistributionPoint {

    private String serviceName;
    private long count;
}
