package com.example.project114.backend;

import com.example.project114.AppData;

import java.time.LocalDate;
import java.time.LocalTime;

public class BookingService {
    //config
    int gapTime = 90; //minute
    public final LocalTime openTime = LocalTime.of(10,0);
    public final LocalTime closeTime = LocalTime.of(20,30);
    public final int maxBookingDay = 3;
    public final byte maxGuest = 6;

    int timeIndex = 0;
    String []timeList;
    LocalDate canBookingDate;
    LocalDate maxBookingDate;

    public BookingService() {
        while (closeTime.isAfter(openTime.plusMinutes(gapTime * timeIndex))){
            timeIndex++;
        }
        timeList = new String[timeIndex];
        //คำนวนเวลาแต่ละช่วง
        for (int i = 0; i < timeIndex; i++){
            timeList[i] = openTime.plusMinutes(gapTime * i).toString();
        }

        if (LocalTime.now().isAfter(closeTime)){
            canBookingDate = LocalDate.now().plusDays(1);
        }
        else {
            canBookingDate = LocalDate.now();
        }
        maxBookingDate = canBookingDate.plusDays(maxBookingDay);
    }

    public LocalTime canBookingTime(LocalTime time){ //หาช่วงเวลาที่จองได้
        for (int i = 0;i < timeIndex;i++){
            if (time.isBefore(openTime.plusMinutes(gapTime * i))){
                return openTime.plusMinutes(gapTime * i);
            }
        }
        return openTime;
    }

    public Reservation book(LocalDate date, LocalTime time, Customer customer, byte guest){
        Reservation data;
        if (!AppData.allBookingData.isTableFull(date,time)) {
            data = new Reservation(customer,date,time,guest,AppData.allBookingData.emptyTableNo(date,time));
            AppData.allBookingData.addReservation(data);
            return data;
        }
        else {
            return null;
        }
    }

    public String[] getTimeList(){
        return timeList;
    }
    public LocalDate getCanBookingDate(){
        return canBookingDate;
    }
    public LocalDate getMaxBookingDate() {
        return maxBookingDate;
    }
}
