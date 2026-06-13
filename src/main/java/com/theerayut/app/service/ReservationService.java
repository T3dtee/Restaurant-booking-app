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
        Optional<Reservation> reusable = reservationMap.values().stream()
                .filter(res -> res.getDate().equals(date) && res.getTime().equals(time))
                .filter(res -> res.getStatus() == ReservationStatus.CANCELLED || res.getStatus() == ReservationStatus.EXPIRED)
                .findFirst();

        if (reusable.isPresent()) {
            int tableNo = reusable.get().getTableNo();
            reservationMap.remove(reusable.get());
            return tableNo;
        }

        return countByDateTime(date, time) + 1;
    }

    public List<Reservation> getAllReservations() {
        return reservationMap.values().stream().toList();
    }

    public List<Reservation> getReservationsByCustomer(Customer customer) {
        updateReservations();
        return reservationMap.values().stream()
                .filter(r -> customer.getId().equals(r.getCustomerId()))
                .toList();
    }

    public List<Reservation> getReservationsByDate(LocalDate date) {
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
