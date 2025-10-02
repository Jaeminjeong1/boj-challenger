package com.example.bojchallenge.group.service;

import com.example.bojchallenge.group.dto.GroupMemberStatusResponse;
import com.example.bojchallenge.group.entity.Group;
import com.example.bojchallenge.group.entity.GroupMember;
import com.example.bojchallenge.group.entity.Punishment;
import com.example.bojchallenge.group.repository.GroupMemberRepository;
import com.example.bojchallenge.group.repository.PunishmentRepository;
import com.example.bojchallenge.solve.dto.TodaySolveSummary;
import com.example.bojchallenge.solve.service.SolvedService;
import com.example.bojchallenge.user.entity.User;
import com.example.bojchallenge.user.service.UserService;
import com.example.bojchallenge.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PunishService {

    private final GroupMemberRepository groupMemberRepository;
    private final PunishmentRepository punishmentRepository;
    private final SolvedService solvedService;
    private final UserService userService;

    @Transactional
    public List<GroupMemberStatusResponse> computeGroupToday(Long groupId) {
        LocalDate today = DateUtils.today();
        List<GroupMember> members = groupMemberRepository.findByGroupId(groupId);
        List<GroupMemberStatusResponse> responses = new ArrayList<>();
        for (GroupMember member : members) {
            User user = member.getUser();
            TodaySolveSummary summary = solvedService.computeTodaySolved(user);
            userService.updateSolveStatus(user, summary.solvedSomething(), today);
            boolean punished = summary.solvedCount() == 0;
            upsertPunishment(member.getGroup(), user, today, punished);
            responses.add(new GroupMemberStatusResponse(
                user.getId(),
                user.getNickname(),
                summary.solvedCount(),
                List.copyOf(summary.problemNumbers()),
                punished,
                member.isOwner()
            ));
        }
        return responses;
    }

    @Transactional
    public void upsertPunishment(Group group, User user, LocalDate date, boolean punished) {
        Punishment entity = punishmentRepository.findByGroupIdAndUserIdAndDate(group.getId(), user.getId(), date)
            .orElseGet(() -> punishmentRepository.save(Punishment.builder()
                .group(group)
                .user(user)
                .date(date)
                .punished(punished)
                .build()));
        entity.updatePunished(punished);
    }
}
