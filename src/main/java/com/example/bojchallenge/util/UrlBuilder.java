package com.example.bojchallenge.util;

public final class UrlBuilder {

    private UrlBuilder() {
    }

    public static String bojProblemUrl(int number) {
        return "https://www.acmicpc.net/problem/" + number;
    }
}
