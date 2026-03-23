package com.example.project114;

import com.example.project114.backend.Customer;
import com.example.project114.backend.Reservation;
import com.example.project114.backend.ReservationService;

public class AppData {
    public static Customer loginUserData;
    public static Reservation bookingData;
    public static staffLoginData loginStaffData;
    public static ReservationService allBookingData = new ReservationService();
}

class staffLoginData{
    private String username;
    private String password;

    public staffLoginData(String username, String password){
        this.username = username;
        this.password = password;
    }
    public String getUsername(){
        return username;
    }
    public String getPassword(){
        return password;
    }
}