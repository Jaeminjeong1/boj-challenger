package com.example.bojchallenge.user.dto;

import java.util.List;

public record DashboardResponse(String bojHandle, int streak, List<Integer> todaySolvedProblems, int todaySolvedCount) {
}
