package com.theerayut.app.service;

import com.theerayut.app.AppData;
import com.theerayut.app.model.Customer;
import com.theerayut.app.model.Reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class BookingService {

    /** เหตุผลที่วันหนึ่ง ๆ จองได้หรือไม่ได้ — UI เอาไปตัดสินใจแสดงผลได้โดยไม่ต้องรู้กฎเอง */
    public enum DateStatus {
        OPEN,        // จองได้
        CLOSED_DAY,  // ร้านหยุดประจำสัปดาห์
        PAST,        // ผ่านมาแล้ว หรือวันนี้ที่เลยเวลาจองรอบสุดท้ายไปแล้ว
        TOO_FAR      // เกินระยะจองล่วงหน้า
    }

    private LocalTime[] timeSlotList;
    private final AtomicInteger idCounter = new AtomicInteger(1);

    public BookingService() {
        recalculate();
        initIdCounter();
    }

    // ตั้ง counter ต่อจาก id สูงสุดที่มีอยู่ กันเลขซ้ำหลังเปิดแอปใหม่
    private void initIdCounter() {
        int maxId = AppData.allBookingData.getAllReservations().stream()
                .map(Reservation::getReservationId)        // เช่น "RES-0007"
                .filter(id -> id != null && id.startsWith("RES-"))
                .mapToInt(id -> {
                    try {
                        return Integer.parseInt(id.substring(4));
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                })
                .max()
                .orElse(0);
        idCounter.set(maxId + 1);
    }

    /** สร้างช่วงเวลาใหม่จาก config — เรียกเมื่อ admin แก้เวลาเปิด/ปิด/gap */
    public void recalculate() {
        int gapTime = AppData.config.getGapTimeMinutes();
        LocalTime openTime = AppData.config.getOpenTime();
        LocalTime closeTime = AppData.config.getCloseTime();

        List<LocalTime> slots = new ArrayList<>();
        for (LocalTime slot = openTime; closeTime.isAfter(slot); slot = slot.plusMinutes(gapTime)) {
            slots.add(slot);
        }
        timeSlotList = slots.toArray(new LocalTime[0]);
    }

    // ---- ขอบเขตวันที่จองได้ (คิดสดจากเวลาปัจจุบันทุกครั้ง ไม่ cache กันค่าค้างข้ามวัน) ----

    /** วันเริ่มนับ window — วันนี้ หรือพรุ่งนี้ถ้าเลยรอบจองสุดท้ายของวันนี้ไปแล้ว */
    private LocalDate windowStart() {
        LocalTime lastSlot = AppData.config.getCloseTime()
                .minusMinutes(AppData.config.getGapTimeMinutes());

        return LocalTime.now().isAfter(lastSlot)
                ? LocalDate.now().plusDays(1)
                : LocalDate.now();
    }

    /** วันแรกที่จองได้ — เลื่อนจาก windowStart ข้ามวันหยุดไปจนเจอวันที่ร้านเปิด */
    public LocalDate getFirstBookableDate() {
        LocalDate date = windowStart();
        // cap 7 รอบ เผื่อ config.json ถูกแก้มือจนหยุดครบสัปดาห์ (UI กันไว้แล้ว)
        for (int i = 0; i < 7 && isClosed(date); i++) {
            date = date.plusDays(1);
        }
        return date;
    }

    /** วันสุดท้ายที่จองได้ — นับวันปฏิทินจาก windowStart วันหยุดที่คั่นอยู่ก็กินโควตาไปด้วย */
    public LocalDate getLastBookableDate() {
        LocalDate last = windowStart().plusDays(AppData.config.getMaxAdvanceDays());
        // วันหยุดยาวติดกันอาจดัน first เลย last ไป — ยืดให้ครอบ first ไว้ จะได้ไม่ตันจนจองไม่ได้เลย
        LocalDate first = getFirstBookableDate();
        return last.isBefore(first) ? first : last;
    }

    public boolean isClosed(LocalDate date) {
        return AppData.config.isClosedOn(date.getDayOfWeek());
    }

    /**
     * ลำดับการตัดสินอยู่ที่นี่ที่เดียว: วันหยุดมาก่อนเสมอ ไม่ว่าจะอยู่ในอดีตหรืออนาคต
     * ปฏิทินจึงย้อมวันหยุดสีเดียวกันทั้งเดือน ส่วนการจองยังถูกปิดอยู่ดีเพราะอนุญาตเฉพาะ OPEN
     */
    public DateStatus statusOf(LocalDate date) {
        if (isClosed(date))                        return DateStatus.CLOSED_DAY;
        if (date.isAfter(getLastBookableDate()))   return DateStatus.TOO_FAR;
        if (date.isBefore(getFirstBookableDate())) return DateStatus.PAST;
        return DateStatus.OPEN;
    }

    // ---- ช่วงเวลา ----

    /** ช่องเวลาแรกของวันนั้นที่จองได้จริง — ว่างเมื่อเต็มหรือเลยเวลาไปหมดแล้ว */
    public Optional<LocalTime> firstAvailableSlot(LocalDate date) {
        for (LocalTime slot : timeSlotList) {
            if (timeSlotAvailable(date, slot)) return Optional.of(slot);
        }
        return Optional.empty();
    }

    /** นิยามเดียวของ "จองได้จริง" ที่ book(), ปุ่ม confirm และ time slot ใช้ร่วมกัน */
    public boolean timeSlotAvailable(LocalDate date, LocalTime time) {
        return statusOf(date) == DateStatus.OPEN
                && LocalDateTime.of(date, time).isAfter(LocalDateTime.now())
                && !AppData.allBookingData.isTableFull(date, time);
    }

    public Reservation book(LocalDate date, LocalTime time, Customer customer, byte guest) {
        if (!timeSlotAvailable(date, time)) return null;

        String reservationId = String.format("RES-%04d", idCounter.getAndIncrement());
        Reservation data = new Reservation(reservationId, customer.getId(), date, time, guest,
                AppData.allBookingData.emptyTableNo(date, time));
        AppData.allBookingData.addReservation(data);
        return data;
    }

    public LocalTime[] getTimeSlotList() {
        return timeSlotList;
    }
}
