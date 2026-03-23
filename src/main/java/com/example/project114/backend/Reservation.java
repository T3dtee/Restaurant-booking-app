package com.example.project114.backend;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

//คลาสการจองต่อ1คิว
public class Reservation {

    private Customer customer;
    private LocalDate date;
    private LocalTime time;
    private int guestCount;
    private int queueNumber;
    private String status;
    private LocalDateTime checkInTime;

    // Constructor
    public Reservation(Customer customer,
                       LocalDate date,
                       LocalTime time,
                       int guestCount,
                       int queueNumber
                       ) {

        this.customer = customer;
        this.date = date;
        this.time = time;
        this.guestCount = guestCount;
        this.queueNumber = queueNumber;
        this.status = ReservationStatus.BOOKED;

    }
    public Customer getCustomer() { 
        return customer; 
    }
    public LocalDate getDate() { 
        return date; 
    }
    public LocalTime getTime() {
        return time; 
    }
    public int getGuestCount() { 
        return guestCount; 
    }
    public int getQueueNumber() { 
        return queueNumber; 
    }
    public String getStatus() {
         return status; 
    }

      // method เปลี่ยนสถานะการจอง
    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
    }

    public void checkIn() {
        this.status =ReservationStatus.CHECKED_IN;
        this.checkInTime = LocalDateTime.now();
    }

    public void expire() {
        this.status = ReservationStatus.EXPIRED;
    }

    // ใช้เช็คหมดเวลา 1 ชั่วโมงครึ่ง
    public boolean isOverTime() {
        if (status.equals (ReservationStatus.CHECKED_IN) && checkInTime != null) {
            return checkInTime.plusMinutes(90).isBefore(LocalDateTime.now());
        }
        return false;
    }

}