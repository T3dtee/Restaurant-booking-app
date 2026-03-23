package com.example.project114.backend;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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

        int count = 0;

        for (Reservation r : reservationList) {
            if (r.getDate().equals(date)
                    && r.getTime().equals(time)
                    && !ReservationStatus.CANCELLED.equals(r.getStatus())
                    && !ReservationStatus.EXPIRED.equals(r.getStatus())) {
                count++;
            }
        }

        return count;
    }

    public int emptyTableNo(LocalDate date, LocalTime time){
        for (Reservation res : reservationList){
            if (res.getDate().equals(date) && res.getTime().equals(time))
                if (res.getStatus().equals("CANCELLED") || res.getStatus().equals("EXPIRED")){
                    int queueNum = res.getQueueNumber();
                    reservationList.remove(res);
                    return queueNum;
                }
        }
        return countByDateTime(date,time) + 1;
    }

    public List<Reservation> getAllReservations() {
        return reservationList;
    }

    public void updateExpiredReservations() {
    for (Reservation r : reservationList) {
        if (r.isOverTime()) {
            r.expire();
        }
    }
}
}
