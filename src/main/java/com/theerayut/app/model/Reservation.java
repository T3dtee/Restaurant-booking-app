package com.theerayut.app.model;

import com.theerayut.app.AppData;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

//คลาสการจองต่อ1คิว
public class Reservation {

    private final String reservationId;
    private final String customerId;
    private final LocalDate date;
    private final LocalTime time;
    private final int guestCount;
    private final int tableNo;
    private ReservationStatus status;
    private Person cancelBy;
    private final LocalDateTime bookingTime;
    private LocalDateTime checkInTime;

    private boolean doneBooking = false;
    public boolean isDoneBooking() {
        return doneBooking;
    }
    public void setDoneBooking(boolean doneBooking) {
        this.doneBooking = doneBooking;
    }

    public Reservation (String reservationId,
                        String customerId,
                        LocalDate date,
                        LocalTime time,
                        int guestCount,
                        int queueNumber
                        ) {
        this.reservationId = reservationId;
        this.customerId = customerId;
        this.date = date;
        this.time = time;
        this.guestCount = guestCount;
        this.tableNo = queueNumber;
        this.status = ReservationStatus.BOOKED;
        bookingTime = LocalDateTime.now();
    }

    public Customer getCustomer() {
        Customer customer = AppData.loginService.findCustomerById(customerId);
        if (customer == null) {
            return new Customer("Not fond", "404");
        }
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
    public ReservationStatus getStatus() {
         return status; 
    }
    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }
    public LocalDateTime getBookingTime() {return bookingTime;}
    public Person getCancelBy() {
        return cancelBy;
    }
    public String getCustomerId() {
        return customerId;
    }
    public String getReservationId() {
        return reservationId;
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
        if (status == ReservationStatus.CHECKED_IN && checkInTime != null) {
            return checkInTime.plusMinutes(90).isBefore(LocalDateTime.now());
        }
        return false;
    }

}
