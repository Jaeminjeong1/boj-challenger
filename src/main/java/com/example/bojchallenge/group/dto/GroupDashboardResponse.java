package com.example.bojchallenge.group.dto;

import java.util.List;

public record GroupDashboardResponse(Long groupId, String groupName, String ownerNickname, List<GroupMemberStatusResponse> members) {
}
