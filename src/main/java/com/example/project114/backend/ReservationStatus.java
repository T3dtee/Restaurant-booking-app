package com.example.project114.backend;

// enum สถานะการจอง
public enum ReservationStatus {
    // จองแต่ยังไม่มา
    BOOKED,
    // มาถึงโต๊ะและเริ่มใช้
    CHECKED_IN,
    // ยกเลิกการจอง(ลูกค้ามาช้าเกินเวลา/ไม่มา)
    CANCELLED,
    // หมดเวลาลูกค้าใช้โต๊ะครบ 1 ชั่วโมงครึ่ง
    EXPIRED
}
