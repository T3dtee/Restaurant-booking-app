package com.theerayut.app;

import com.theerayut.app.model.*;
import com.theerayut.app.model.Staff;
import com.theerayut.app.service.*;

public class AppData {
    public static Customer loginUserData;
    public static String bookingId;
    public static Staff loginStaffData;
    public static final RestaurantConfig config = RestaurantConfig.load();
    public static final ReservationService allBookingData = new ReservationService();
    public static final BookingService bookingService = new BookingService();
    public static final LoginService loginService = new LoginService();
    public static final StaffService staffService = new StaffService();
}
