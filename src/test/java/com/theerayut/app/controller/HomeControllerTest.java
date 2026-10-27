package com.theerayut.app.controller;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HomeControllerTest {
    private static final LocalDate TODAY = LocalDate.of(2026, 10, 20);

    @Test
    void formatsTodayAndTomorrowWithTwentyFourHourTime() {
        assertEquals("Today at 13:00", homeController.formatWhen(
                LocalDateTime.of(2026, 10, 20, 13, 0), TODAY));
        assertEquals("Tomorrow at 09:05", homeController.formatWhen(
                LocalDateTime.of(2026, 10, 21, 9, 5), TODAY));
    }

    @Test
    void formatsOtherDaysInEnglish() {
        assertEquals("Thursday at 18:30", homeController.formatWhen(
                LocalDateTime.of(2026, 10, 22, 18, 30), TODAY));
    }
}
