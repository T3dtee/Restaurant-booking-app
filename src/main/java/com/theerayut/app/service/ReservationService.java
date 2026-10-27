package com.theerayut.app.service;

import com.google.gson.reflect.TypeToken;
import com.theerayut.app.AppData;
import com.theerayut.app.model.Customer;
import com.theerayut.app.model.Reservation;
import com.theerayut.app.model.ReservationStatus;
import com.theerayut.app.model.RestaurantConfig;
import com.theerayut.app.util.JsonStorage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class ReservationService {

    private Map<String, Reservation> reservationMap;

    public ReservationService(){
        reservationMap = JsonStorage.load("reservations.json",new TypeToken <Map<String, Reservation>>(){}.getType());
        if (reservationMap == null) reservationMap = new HashMap<>();
        else {
            updateReservations();
        }
    }

    public void addReservation(Reservation reservation) {
        reservationMap.put(reservation.getReservationId(), reservation);
        updateJson();
    }

    public boolean isTableFull(LocalDate date, LocalTime time){
        return countByDateTime(date, time) >= AppData.config.getMaxTables();
    }

    public int countByDateTime(LocalDate date, LocalTime time) {
        return (int) reservationMap.values().stream()
                .filter(r -> r.getDate().equals(date)
                        && r.getTime().equals(time)
                        && r.getStatus() != ReservationStatus.CANCELLED
                        && r.getStatus() != ReservationStatus.EXPIRED)
                .count();
    }

    public int emptyTableNo(LocalDate date, LocalTime time) {
        Set<Integer> activeTables = reservationMap.values().stream()
                .filter(r -> r.getDate().equals(date) && r.getTime().equals(time)
                        && r.getStatus() != ReservationStatus.CANCELLED
                        && r.getStatus() != ReservationStatus.EXPIRED)
                .map(Reservation::getTableNo)
                .collect(Collectors.toSet());

        int tableNo = 1;
        while (activeTables.contains(tableNo)) tableNo++;
        return tableNo;
    }

    public List<Reservation> getAllReservations() {
        updateReservations();
        return reservationMap.values().stream().toList();
    }

    public List<Reservation> getReservationsByCustomer(Customer customer) {
        updateReservations();
        return reservationMap.values().stream()
                .filter(r -> customer.getId().equals(r.getCustomerId()))
                .toList();
    }

    /** Read-only Home summary; selection does not expire or persist reservations. */
    public Optional<Reservation> getHomeReservation(Customer customer, LocalDateTime now) {
        return selectHomeReservation(reservationMap.values(), customer.getId(), now,
                AppData.config.getGapTimeMinutes());
    }

    static Optional<Reservation> selectHomeReservation(Collection<Reservation> reservations,
                                                       String customerId, LocalDateTime now,
                                                       int durationMinutes) {
        return reservations.stream()
                .filter(r -> customerId.equals(r.getCustomerId()))
                .filter(r -> {
                    LocalDateTime start = LocalDateTime.of(r.getDate(), r.getTime());
                    return (r.getStatus() == ReservationStatus.CHECKED_IN
                            || r.getStatus() == ReservationStatus.BOOKED)
                            && !start.plusMinutes(durationMinutes).isBefore(now);
                })
                .min(Comparator.comparing((Reservation r) ->
                                r.getStatus() == ReservationStatus.CHECKED_IN ? 0 : 1)
                        .thenComparing(Reservation::getDate)
                        .thenComparing(Reservation::getTime)
                        .thenComparing(Reservation::getTableNo)
                        .thenComparing(Reservation::getReservationId));
    }

    public List<Reservation> getReservationsByDate(LocalDate date) {
        updateReservations();
        return reservationMap.values().stream()
                .filter(r -> r.getDate().equals(date))
                .sorted(Comparator.comparing(Reservation::getTime)
                        .thenComparing(Reservation::getTableNo))
                .toList();
    }

    public Reservation findReservationById(String id) {
        return reservationMap.get(id);
    }

    public boolean isCustomerBooked(Customer customer, LocalDate date, LocalTime time) {
        for (Reservation r : getReservationsByCustomer(customer)) {
            if (r.getDate().equals(date)
                    && r.getTime().equals(time)
                    && (r.getStatus() == ReservationStatus.BOOKED
                    || r.getStatus() == ReservationStatus.CHECKED_IN))
                return true;
        }
        return false;
    }

    public void updateReservations() {
        for (Reservation r : reservationMap.values().stream().toList()) {
            if (r.getStatus() == ReservationStatus.BOOKED
                    && LocalDateTime.of(r.getDate(), r.getTime()).plusMinutes(RestaurantConfig.load().getGapTimeMinutes()).isBefore(LocalDateTime.now())) {
                r.cancel();
            }
            else if (r.getStatus() == ReservationStatus.CHECKED_IN
                    && LocalDateTime.of(r.getDate(), r.getTime()).plusMinutes(RestaurantConfig.load().getGapTimeMinutes()).isBefore(LocalDateTime.now())) {
                r.expire();
            }
        }
        updateJson();
    }

    public void updateJson(){
        JsonStorage.save(reservationMap, "reservations.json");
    }
}
