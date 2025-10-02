package com.example.bojchallenge.group.service;

import com.example.bojchallenge.global.common.BadRequestException;
import com.example.bojchallenge.group.dto.CreateGroupRequest;
import com.example.bojchallenge.group.dto.JoinGroupRequest;
import com.example.bojchallenge.solve.dto.TodaySolveSummary;
import com.example.bojchallenge.solve.service.SolvedService;
import com.example.bojchallenge.user.entity.User;
import com.example.bojchallenge.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class GroupServiceTest {

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;

    @MockBean
    private SolvedService solvedService;

    @BeforeEach
    void setUp() {
        Mockito.when(solvedService.computeTodaySolved(any()))
            .thenReturn(new TodaySolveSummary(0, List.of()));
    }

    @Test
    void preventDuplicateJoin() {
        User owner = userService.register("owner@example.com", "password123", "owner1");
        groupService.createGroup(owner.getId(), new CreateGroupRequest("Test Group", "1234"));

        User member = userService.register("member@example.com", "password123", "member1");
        groupService.joinGroup(member.getId(), new JoinGroupRequest("1234"));

        assertThatThrownBy(() -> groupService.joinGroup(member.getId(), new JoinGroupRequest("1234")))
            .isInstanceOf(BadRequestException.class);
    }
}
