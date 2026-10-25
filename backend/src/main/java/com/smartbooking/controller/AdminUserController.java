package com.smartbooking.controller;

import com.smartbooking.dto.common.ApiResponse;
import com.smartbooking.dto.user.UserResponse;
import com.smartbooking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public ApiResponse<Page<UserResponse>> listUsers(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.ok(userService.listCustomers(search, pageable));
    }

    @PatchMapping("/{id}/active")
    public ApiResponse<UserResponse> setActive(@PathVariable Long id, @RequestParam boolean active) {
        return ApiResponse.ok(userService.setCustomerActive(id, active));
    }
}
