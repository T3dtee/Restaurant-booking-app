package com.example.project114.backend;

//คลาสสถานะการจอง
public class ReservationStatus {

    //จองแต่ยังไม่มา
    public static final String BOOKED = "BOOKED";

    //มาถึงโต๊ะและเริ่มใช้
    public static final String CHECKED_IN = "CHECKED_IN";

    //ยกเลิกการจอง(ลูกค้ามาช้าเกินเวลา/ไม่มา)
    public static final String CANCELLED = "CANCELLED";

    //(หมดเวลาลูกค้าใช้เวลา ครบ 1.30 ชัวโมง)
    public static final String EXPIRED = "EXPIRED";
}

