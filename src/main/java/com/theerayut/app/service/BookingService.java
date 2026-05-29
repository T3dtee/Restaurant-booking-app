package com.theerayut.app.service;

import com.theerayut.app.AppData;
import com.theerayut.app.model.Customer;
import com.theerayut.app.model.Reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class BookingService {
    //config
    int gapTime = 90; //minute
    public final LocalTime openTime = LocalTime.of(10,0);
    public final LocalTime closeTime = LocalTime.of(20,30);
    public final int maxBookingDay = 3;
    public final byte maxGuest = 6;

    int timeIndex = 0;
    LocalTime [] timeSlotList;
    LocalDate canBookingDate;
    LocalDate maxBookingDate;

    public BookingService() {
        while (closeTime.isAfter(openTime.plusMinutes((long) gapTime * timeIndex))){
            timeIndex++;
        }
        timeSlotList = new LocalTime[timeIndex];
        //คำนวนเวลาแต่ละช่วง
        for (int i = 0; i < timeIndex; i++){
            timeSlotList[i] = openTime.plusMinutes((long) gapTime * i);
        }

        if (LocalTime.now().isAfter(closeTime.minusMinutes(gapTime))){
            canBookingDate = LocalDate.now().plusDays(1);
        }
        else {
            canBookingDate = LocalDate.now();
        }
        maxBookingDate = canBookingDate.plusDays(maxBookingDay);
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
            data = new Reservation(customerId,date,time,guest,AppData.allBookingData.emptyTableNo(date,time));
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
