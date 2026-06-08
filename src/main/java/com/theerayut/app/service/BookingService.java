package com.theerayut.app.service;

import com.theerayut.app.AppData;
import com.theerayut.app.model.Customer;
import com.theerayut.app.model.Reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicInteger;

public class BookingService {
    int timeIndex = 0;
    LocalTime[] timeSlotList;
    LocalDate canBookingDate;
    LocalDate maxBookingDate;
    private final AtomicInteger idCounter = new AtomicInteger(1);

    public BookingService() {
        recalculate();
    }

    public void recalculate() {
        int gapTime = AppData.config.getGapTimeMinutes();
        LocalTime openTime = AppData.config.getOpenTime();
        LocalTime closeTime = AppData.config.getCloseTime();

        timeIndex = 0;
        while (closeTime.isAfter(openTime.plusMinutes((long) gapTime * timeIndex))) {
            timeIndex++;
        }
        timeSlotList = new LocalTime[timeIndex];
        for (int i = 0; i < timeIndex; i++) {
            timeSlotList[i] = openTime.plusMinutes((long) gapTime * i);
        }

        if (LocalTime.now().isAfter(closeTime.minusMinutes(gapTime))) {
            canBookingDate = LocalDate.now().plusDays(1);
        } else {
            canBookingDate = LocalDate.now();
        }
        maxBookingDate = canBookingDate.plusDays(AppData.config.getMaxAdvanceDays());
    }

    public LocalTime canBookingTime(LocalDate date, LocalTime startTime) {
        LocalTime searchFrom;
        if (date.isAfter(LocalDate.now())) {
            searchFrom = LocalTime.MIN;
        } else {
            searchFrom = startTime;
        }
        int i = 0;
        while (i < timeIndex - 1) {
            if (searchFrom.isBefore(timeSlotList[i]) && timeSlotAvailable(date, timeSlotList[i])) {
                break;
            }
            i++;
        }
        return timeSlotList[i];
    }

    public Reservation book(LocalDate date, LocalTime time, Customer customer, byte guest){
        Reservation data;
        String customerId = customer.getId();
        if (timeSlotAvailable(date, time)) {
            String reservationId = String.format("RES-%04d", idCounter.getAndIncrement());
            data = new Reservation(reservationId,customerId,date,time,guest,AppData.allBookingData.emptyTableNo(date,time));
            AppData.allBookingData.addReservation(data);
            return data;
        }
        else {
            return null;
        }
    }

    public boolean timeSlotAvailable(LocalDate date, LocalTime time){
        return LocalDateTime.of(date, time).isAfter(LocalDateTime.now()) && !AppData.allBookingData.isTableFull(date, time);
    }

    public LocalTime[] getTimeSlotList(){
        return timeSlotList;
    }
    public LocalDate getCanBookingDate(){
        return canBookingDate;
    }
    public LocalDate getMaxBookingDate() {
        return maxBookingDate;
    }
}
