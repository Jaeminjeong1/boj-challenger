package com.example.bojchallenge.user.controller;

import com.example.bojchallenge.global.common.ApiResponse;
import com.example.bojchallenge.global.common.UnauthorizedException;
import com.example.bojchallenge.global.security.SecurityUserContextHolder;
import com.example.bojchallenge.user.dto.DashboardResponse;
import com.example.bojchallenge.user.service.UserDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class UserController {

    private final UserDashboardService dashboardService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardResponse>> dashboard() {
        Long userId = SecurityUserContextHolder.getCurrentUserId();
        if (userId == null) {
            throw new UnauthorizedException("Unauthorized");
        }
        DashboardResponse response = dashboardService.getDashboard(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
