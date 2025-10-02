package com.example.bojchallenge.group.controller;

import com.example.bojchallenge.global.common.ApiResponse;
import com.example.bojchallenge.global.common.UnauthorizedException;
import com.example.bojchallenge.global.security.SecurityUserContextHolder;
import com.example.bojchallenge.group.dto.CreateGroupRequest;
import com.example.bojchallenge.group.dto.GroupCardResponse;
import com.example.bojchallenge.group.dto.GroupDashboardResponse;
import com.example.bojchallenge.group.dto.JoinGroupRequest;
import com.example.bojchallenge.group.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<ApiResponse<GroupDashboardResponse>> createGroup(@Valid @RequestBody CreateGroupRequest request) {
        Long userId = requireUserId();
        GroupDashboardResponse response = groupService.createGroup(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/join")
    public ResponseEntity<ApiResponse<Void>> joinGroup(@Valid @RequestBody JoinGroupRequest request) {
        Long userId = requireUserId();
        groupService.joinGroup(userId, request);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<GroupCardResponse>>> myGroups() {
        Long userId = requireUserId();
        List<GroupCardResponse> response = groupService.getMyGroups(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<ApiResponse<GroupDashboardResponse>> groupDashboard(@PathVariable Long groupId) {
        Long userId = requireUserId();
        GroupDashboardResponse response = groupService.getGroupDashboard(userId, groupId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{groupId}/refresh")
    public ResponseEntity<ApiResponse<GroupDashboardResponse>> refresh(@PathVariable Long groupId) {
        Long userId = requireUserId();
        GroupDashboardResponse response = groupService.refreshGroup(userId, groupId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    private Long requireUserId() {
        Long userId = SecurityUserContextHolder.getCurrentUserId();
        if (userId == null) {
            throw new UnauthorizedException("Unauthorized");
        }
        return userId;
    }
}
