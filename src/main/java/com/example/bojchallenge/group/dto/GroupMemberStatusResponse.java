package com.example.bojchallenge.group.dto;

import java.util.List;

public record GroupMemberStatusResponse(
    Long userId,
    String nickname,
    int todaySolvedCount,
    List<Integer> todaySolvedProblems,
    boolean punished,
    boolean owner
) {
}
