package com.theerayut.app.model;

import java.time.LocalTime;

public class RestaurantConfig {
    private int maxTables = 10;
    private int maxGuest = 6;
    private LocalTime openTime = LocalTime.of(10, 0);
    private LocalTime closeTime = LocalTime.of(20, 30);
    private int gapTimeMinutes = 90;
    private int maxAdvanceDays = 3;

    public int getMaxTables() { return maxTables; }
    public void setMaxTables(int maxTables) { this.maxTables = maxTables; }

    public int getMaxGuest() { return maxGuest; }
    public void setMaxGuest(int maxGuest) { this.maxGuest = maxGuest; }

    public LocalTime getOpenTime() { return openTime; }
    public void setOpenTime(LocalTime openTime) { this.openTime = openTime; }

    public LocalTime getCloseTime() { return closeTime; }
    public void setCloseTime(LocalTime closeTime) { this.closeTime = closeTime; }

    public int getGapTimeMinutes() { return gapTimeMinutes; }
    public void setGapTimeMinutes(int gapTimeMinutes) { this.gapTimeMinutes = gapTimeMinutes; }

    public int getMaxAdvanceDays() { return maxAdvanceDays; }
    public void setMaxAdvanceDays(int maxAdvanceDays) { this.maxAdvanceDays = maxAdvanceDays; }
}
