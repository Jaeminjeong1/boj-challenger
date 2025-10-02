package com.example.bojchallenge.user.service;

import com.example.bojchallenge.solve.dto.TodaySolveSummary;
import com.example.bojchallenge.solve.service.SolvedService;
import com.example.bojchallenge.user.dto.DashboardResponse;
import com.example.bojchallenge.user.entity.User;
import com.example.bojchallenge.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserDashboardService {

    private final UserService userService;
    private final SolvedService solvedService;

    @Transactional
    public DashboardResponse getDashboard(Long userId) {
        User user = userService.getById(userId);
        TodaySolveSummary summary = solvedService.computeTodaySolved(user);
        LocalDate today = DateUtils.today();
        userService.updateSolveStatus(user, summary.solvedSomething(), today);
        return userService.toDashboard(user, summary.problemNumbers());
    }
}
