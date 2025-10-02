package com.example.bojchallenge.solve.dto;

import java.util.List;

public record TodaySolveSummary(int solvedCount, List<Integer> problemNumbers) {

    public boolean solvedSomething() {
        return solvedCount > 0;
    }
}
