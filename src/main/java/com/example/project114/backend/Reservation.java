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
    private int tableNo;
    private String status;
    private Person cancelBy;
    private LocalDateTime bookingTime;
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
        this.tableNo = queueNumber;
        this.status = ReservationStatus.BOOKED;
        bookingTime = LocalDateTime.now();
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
    public int getTableNo() {
        return tableNo;
    }
    public String getStatus() {
         return status; 
    }
    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }
    public LocalDateTime getBookingTime() {return bookingTime;}
    public Person getCancelBy() {
        return cancelBy;
    }

      // method เปลี่ยนสถานะการจอง
    public void cancel(Person cancelBy) {
        this.status = ReservationStatus.CANCELLED;
        this.cancelBy = cancelBy;
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