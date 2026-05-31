package com.theerayut.app;

import com.theerayut.app.model.*;
import com.theerayut.app.service.*;

public class AppData {
    public static Customer loginUserData;
    public static Reservation bookingData;
    public static StaffLogin loginStaffData;
    public static final ReservationService allBookingData = new ReservationService();
    public static final BookingService bookingService = new BookingService();
    public static final LoginService loginService = new LoginService();
}
