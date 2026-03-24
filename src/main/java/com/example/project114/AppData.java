package com.example.project114;

import com.example.project114.backend.*;

public class AppData {
    public static Customer loginUserData;
    public static Reservation bookingData;
    public static StaffLogin loginStaffData;
    public static final ReservationService allBookingData = new ReservationService();
    public static final BookingService bookingService = new BookingService();
}
