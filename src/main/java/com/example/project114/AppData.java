package com.example.project114;

import com.example.project114.backend.Customer;
import com.example.project114.backend.Reservation;
import com.example.project114.backend.ReservationService;
import com.example.project114.backend.StaffLogin;

public class AppData {
    public static Customer loginUserData;
    public static Reservation bookingData;
    public static StaffLogin loginStaffData;
    public static ReservationService allBookingData = new ReservationService();
}
