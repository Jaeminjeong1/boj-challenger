package com.example.bojchallenge.group.service;

import com.example.bojchallenge.global.common.BadRequestException;
import com.example.bojchallenge.global.common.ConflictException;
import com.example.bojchallenge.global.common.NotFoundException;
import com.example.bojchallenge.group.dto.CreateGroupRequest;
import com.example.bojchallenge.group.dto.GroupCardResponse;
import com.example.bojchallenge.group.dto.GroupDashboardResponse;
import com.example.bojchallenge.group.dto.GroupMemberStatusResponse;
import com.example.bojchallenge.group.dto.JoinGroupRequest;
import com.example.bojchallenge.group.entity.Group;
import com.example.bojchallenge.group.entity.GroupMember;
import com.example.bojchallenge.group.entity.GroupRole;
import com.example.bojchallenge.group.repository.GroupMemberRepository;
import com.example.bojchallenge.group.repository.GroupRepository;
import com.example.bojchallenge.solve.service.SolvedService;
import com.example.bojchallenge.user.entity.User;
import com.example.bojchallenge.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserService userService;
    private final SolvedService solvedService;
    private final PunishService punishService;

    @Transactional
    public GroupDashboardResponse createGroup(Long ownerId, CreateGroupRequest request) {
        validateGroupRequest(request);
        User owner = userService.getById(ownerId);
        Group group = groupRepository.save(Group.builder()
            .name(request.name())
            .pin4(request.pin4())
            .owner(owner)
            .build());
        groupMemberRepository.save(GroupMember.builder()
            .group(group)
            .user(owner)
            .role(GroupRole.OWNER)
            .joinedAt(LocalDateTime.now())
            .build());
        List<GroupMemberStatusResponse> members = punishService.computeGroupToday(group.getId());
        return new GroupDashboardResponse(group.getId(), group.getName(), group.getOwner().getNickname(), members);
    }

    private void validateGroupRequest(CreateGroupRequest request) {
        if (groupRepository.existsByName(request.name())) {
            throw new ConflictException("Group name already exists");
        }
        if (groupRepository.existsByPin4(request.pin4())) {
            throw new ConflictException("PIN already in use");
        }
    }

    @Transactional
    public void joinGroup(Long userId, JoinGroupRequest request) {
        Group group = groupRepository.findByPin4(request.pin4())
            .orElseThrow(() -> new NotFoundException("Group not found"));
        if (groupMemberRepository.findByGroupIdAndUserId(group.getId(), userId).isPresent()) {
            throw new BadRequestException("Already joined this group");
        }
        User user = userService.getById(userId);
        groupMemberRepository.save(GroupMember.builder()
            .group(group)
            .user(user)
            .role(GroupRole.MEMBER)
            .joinedAt(LocalDateTime.now())
            .build());
    }

    public List<GroupCardResponse> getMyGroups(Long userId) {
        List<GroupMember> memberships = groupMemberRepository.findByUserId(userId);
        return memberships.stream()
            .map(member -> {
                Group group = member.getGroup();
                long memberCount = groupMemberRepository.countByGroupId(group.getId());
                return new GroupCardResponse(group.getId(), group.getName(), group.getOwner().getNickname(), (int) memberCount);
            })
            .toList();
    }

    public GroupDashboardResponse getGroupDashboard(Long userId, Long groupId) {
        ensureMembership(userId, groupId);
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new NotFoundException("Group not found"));
        List<GroupMemberStatusResponse> members = punishService.computeGroupToday(groupId);
        return new GroupDashboardResponse(group.getId(), group.getName(), group.getOwner().getNickname(), members);
    }

    @Transactional
    public GroupDashboardResponse refreshGroup(Long userId, Long groupId) {
        GroupMember membership = ensureMembership(userId, groupId);
        Group group = membership.getGroup();
        groupMemberRepository.findByGroupId(groupId).forEach(member -> solvedService.refreshCache(member.getUser().getBojHandle()));
        List<GroupMemberStatusResponse> members = punishService.computeGroupToday(groupId);
        return new GroupDashboardResponse(group.getId(), group.getName(), group.getOwner().getNickname(), members);
    }

    private GroupMember ensureMembership(Long userId, Long groupId) {
        return groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
            .orElseThrow(() -> new NotFoundException("Group membership not found"));
    }
}
