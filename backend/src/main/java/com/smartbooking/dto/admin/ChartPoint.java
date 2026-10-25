package com.smartbooking.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ChartPoint {

    private LocalDate date;
    private long count;
}
