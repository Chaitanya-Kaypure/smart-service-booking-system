package com.smartbooking.controller;

import com.smartbooking.dto.common.ApiResponse;
import com.smartbooking.dto.queue.MyQueueStatusResponse;
import com.smartbooking.dto.queue.QueueSnapshotResponse;
import com.smartbooking.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/queue")
@RequiredArgsConstructor
public class QueueController {

    private final QueueService queueService;

    @GetMapping("/{serviceId}")
    public ApiResponse<QueueSnapshotResponse> queueSnapshot(
            @PathVariable Long serviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.ok(queueService.getQueueSnapshot(serviceId, date));
    }

    @GetMapping("/my")
    public ApiResponse<MyQueueStatusResponse> myQueueStatus() {
        return ApiResponse.ok(queueService.getMyQueueStatus());
    }
}
