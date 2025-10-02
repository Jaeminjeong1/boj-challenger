package com.example.bojchallenge.util;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;

public final class DateUtils {

    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Seoul");
    private static Clock clock = Clock.system(ZONE_ID);

    private DateUtils() {
    }

    public static LocalDate today() {
        return LocalDate.now(clock);
    }

    public static ZoneId zone() {
        return ZONE_ID;
    }

    public static void overrideClock(Clock customClock) {
        clock = customClock;
    }

    public static void resetClock() {
        clock = Clock.system(ZONE_ID);
    }
}
