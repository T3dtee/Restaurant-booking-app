package com.example.project114.backend;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ReservationService {
    public final int MAX_TABLES = 10;

    private List<Reservation> reservationList = new ArrayList<>();

    // เพิ่มการจอง
    public void addReservation(Reservation reservation) {
        if (!isTableFull(reservation.getDate(),reservation.getTime()))
            reservationList.add(reservation);
    }

    public boolean isTableFull(LocalDate date, LocalTime time){
        return countByDateTime(date, time) >= MAX_TABLES;
    }

    // นับจำนวนโต๊ะในวันและเวลาเดียวกัน
    public int countByDateTime(LocalDate date, LocalTime time) {
        return (int) reservationList.stream()
                .filter(r -> r.getDate().equals(date)
                        && r.getTime().equals(time)
                        && r.getStatus() != ReservationStatus.CANCELLED
                        && r.getStatus() != ReservationStatus.EXPIRED)
                .count();
    }

    public int emptyTableNo(LocalDate date, LocalTime time) {
        Optional<Reservation> reusable = reservationList.stream()
                .filter(res -> res.getDate().equals(date) && res.getTime().equals(time))
                .filter(res -> res.getStatus() == ReservationStatus.CANCELLED || res.getStatus() == ReservationStatus.EXPIRED)
                .findFirst();

        if (reusable.isPresent()) {
            int tableNo = reusable.get().getTableNo();
            reservationList.remove(reusable.get());
            return tableNo;
        }

        return countByDateTime(date, time) + 1;
    }

    public List<Reservation> getAllReservations() {
        return reservationList;
    }

    public List<Reservation> getReservationsByCustomer(Customer customer) {
        return reservationList.stream()
                .filter(r -> customer.getName().equals(r.getCustomer().getName()))
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

    public void updateExpiredReservations() {
    for (Reservation r : reservationList) {
        if (r.isOverTime()) {
            r.expire();
        }
    }
}
}
