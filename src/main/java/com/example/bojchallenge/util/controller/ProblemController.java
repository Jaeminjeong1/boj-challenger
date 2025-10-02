package com.example.bojchallenge.util.controller;

import com.example.bojchallenge.global.common.ApiResponse;
import com.example.bojchallenge.util.UrlBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/problems")
public class ProblemController {

    @GetMapping("/{number}/url")
    public ResponseEntity<ApiResponse<Map<String, String>>> getProblemUrl(@PathVariable int number) {
        String url = UrlBuilder.bojProblemUrl(number);
        return ResponseEntity.ok(ApiResponse.success(Map.of("url", url)));
    }
}
