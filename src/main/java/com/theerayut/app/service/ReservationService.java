package com.theerayut.app.service;

import com.theerayut.app.model.Customer;
import com.theerayut.app.model.Reservation;
import com.theerayut.app.model.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class ReservationService {
    public final int MAX_TABLES = 10;

    private final Map<String, Reservation> reservationMap = new HashMap<>();

    public void addReservation(Reservation reservation) {
        reservationMap.put(reservation.getReservationId(), reservation);
    }

    public boolean isTableFull(LocalDate date, LocalTime time){
        return countByDateTime(date, time) >= MAX_TABLES;
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
        return reservationMap.values().stream()
                .filter(r -> customer.getId().equals(r.getCustomerId()))
                .sorted(Comparator.comparing((Reservation r) -> {
                        if (r.getStatus() == ReservationStatus.BOOKED) return -1;
                        else if (r.getStatus() == ReservationStatus.CHECKED_IN) return 0;
                        else return 1;}) //ให้ Booked ขึ้นก่อน ตามด้วย Checked in
                        .thenComparing(Reservation::getDate)
                        .thenComparing(Reservation::getTime)
                        .thenComparing(Reservation::getTableNo)
                )
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

//    public void updateExpiredReservations() {
//        for (Reservation r : reservationList) {
//            if (r.isOverTime()) {
//                r.expire();
//            }
//        }
//    }
}
