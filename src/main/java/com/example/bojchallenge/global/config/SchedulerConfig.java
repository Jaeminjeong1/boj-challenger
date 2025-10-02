package com.example.bojchallenge.global.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.ZoneId;
import java.util.TimeZone;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    @PostConstruct
    void setTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Asia/Seoul")));
    }
}
