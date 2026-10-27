package com.theerayut.app.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.theerayut.app.model.Reservation;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReservationServiceTest {
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 10, 20, 13, 30);
    private static final String CUSTOMER = "0800000001";
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class,
                    (JsonDeserializer<LocalDate>) (json, type, context) -> LocalDate.parse(json.getAsString()))
            .registerTypeAdapter(LocalTime.class,
                    (JsonDeserializer<LocalTime>) (json, type, context) -> LocalTime.parse(json.getAsString()))
            .registerTypeAdapter(LocalDateTime.class,
                    (JsonDeserializer<LocalDateTime>) (json, type, context) -> LocalDateTime.parse(json.getAsString()))
            .create();

    @Test
    void checkedInVisitTakesPriorityOverFutureBooking() {
        Reservation booked = reservation("future", CUSTOMER, "2026-10-20", "15:00", "BOOKED");
        Reservation current = reservation("current", CUSTOMER, "2026-10-20", "13:00", "CHECKED_IN");

        assertSame(current, ReservationService.selectHomeReservation(
                List.of(booked, current), CUSTOMER, NOW, 90).orElseThrow());
    }

    @Test
    void checkedInVisitRemainsThroughExactConfiguredEndTime() {
        Reservation current = reservation("current", CUSTOMER, "2026-10-20", "13:00", "CHECKED_IN");
        LocalDateTime end = LocalDateTime.of(2026, 10, 20, 13, 45);

        assertSame(current, ReservationService.selectHomeReservation(
                List.of(current), CUSTOMER, end.minusNanos(1), 45).orElseThrow());
        assertSame(current, ReservationService.selectHomeReservation(
                List.of(current), CUSTOMER, end, 45).orElseThrow());
        assertTrue(ReservationService.selectHomeReservation(
                List.of(current), CUSTOMER, end.plusNanos(1), 45).isEmpty());
        assertEquals("CHECKED_IN", current.getStatus().name(), "Home selection must not mutate reservations");
    }

    @Test
    void fallsBackToNextBookingAfterCheckedInVisitEnds() {
        Reservation ended = reservation("ended", CUSTOMER, "2026-10-20", "11:00", "CHECKED_IN");
        Reservation next = reservation("next", CUSTOMER, "2026-10-20", "15:00", "BOOKED");

        assertSame(next, ReservationService.selectHomeReservation(
                List.of(ended, next), CUSTOMER, NOW, 90).orElseThrow());
    }

    @Test
    void selectsEarliestFutureBookingRegardlessOfInputOrder() {
        Reservation tomorrow = reservation("tomorrow", CUSTOMER, "2026-10-21", "10:00", "BOOKED");
        Reservation laterToday = reservation("later", CUSTOMER, "2026-10-20", "18:00", "BOOKED");
        Reservation next = reservation("next", CUSTOMER, "2026-10-20", "15:00", "BOOKED");

        assertSame(next, ReservationService.selectHomeReservation(
                List.of(tomorrow, laterToday, next), CUSTOMER, NOW, 90).orElseThrow());
    }

    @Test
    void bookedReservationRemainsThroughExactConfiguredEndTime() {
        Reservation booked = reservation("booked", CUSTOMER, "2026-10-20", "13:00", "BOOKED");
        LocalDateTime start = LocalDateTime.of(2026, 10, 20, 13, 0);
        LocalDateTime end = LocalDateTime.of(2026, 10, 20, 13, 45);

        assertSame(booked, ReservationService.selectHomeReservation(
                List.of(booked), CUSTOMER, start, 45).orElseThrow());
        assertSame(booked, ReservationService.selectHomeReservation(
                List.of(booked), CUSTOMER, NOW, 45).orElseThrow());
        assertSame(booked, ReservationService.selectHomeReservation(
                List.of(booked), CUSTOMER, end.minusNanos(1), 45).orElseThrow());
        assertSame(booked, ReservationService.selectHomeReservation(
                List.of(booked), CUSTOMER, end, 45).orElseThrow());
        assertTrue(ReservationService.selectHomeReservation(
                List.of(booked), CUSTOMER, end.plusNanos(1), 45).isEmpty());
        assertEquals("BOOKED", booked.getStatus().name(), "Home selection must not mutate reservations");
    }

    @Test
    void ongoingBookedReservationIsShownBeforeFutureBooking() {
        Reservation ongoing = reservation("ongoing", CUSTOMER, "2026-10-20", "13:00", "BOOKED");
        Reservation future = reservation("future", CUSTOMER, "2026-10-20", "15:00", "BOOKED");
        Reservation checkedIn = reservation("checked-in", CUSTOMER, "2026-10-20", "13:15", "CHECKED_IN");

        assertSame(ongoing, ReservationService.selectHomeReservation(
                List.of(future, ongoing), CUSTOMER, NOW, 90).orElseThrow());
        assertSame(checkedIn, ReservationService.selectHomeReservation(
                List.of(ongoing, future, checkedIn), CUSTOMER, NOW, 90).orElseThrow());
    }

    @Test
    void excludesOtherCustomersAndInactiveStatuses() {
        List<Reservation> reservations = List.of(
                reservation("other-current", "0800000002", "2026-10-20", "13:00", "CHECKED_IN"),
                reservation("other-future", "0800000002", "2026-10-20", "15:00", "BOOKED"),
                reservation("cancelled", CUSTOMER, "2026-10-20", "15:00", "CANCELLED"),
                reservation("expired", CUSTOMER, "2026-10-20", "13:00", "EXPIRED"),
                reservation("ended", CUSTOMER, "2026-10-20", "11:00", "BOOKED"));

        assertTrue(ReservationService.selectHomeReservation(reservations, CUSTOMER, NOW, 90).isEmpty());
    }

    @Test
    void selectsEarliestCheckedInVisitWhenSeveralQualify() {
        Reservation later = reservation("later", CUSTOMER, "2026-10-20", "13:30", "CHECKED_IN");
        Reservation earlier = reservation("earlier", CUSTOMER, "2026-10-20", "13:00", "CHECKED_IN");

        assertSame(earlier, ReservationService.selectHomeReservation(
                List.of(later, earlier), CUSTOMER, NOW, 90).orElseThrow());
    }

    @Test
    void noReservationsProducesEmptyState() {
        assertTrue(ReservationService.selectHomeReservation(List.of(), CUSTOMER, NOW, 90).isEmpty());
    }

    // Deserialize stored values without calling checkIn(), which writes AppData to local JSON.
    private Reservation reservation(String id, String customerId, String date, String time, String status) {
        return GSON.fromJson("""
                {"reservationId":"%s","customerId":"%s","date":"%s","time":"%s",
                 "guestCount":2,"tableNo":3,"status":"%s"}
                """.formatted(id, customerId, date, time, status), Reservation.class);
    }
}
