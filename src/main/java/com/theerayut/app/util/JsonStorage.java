package com.theerayut.app.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class JsonStorage {
    // โฟลเดอร์เก็บไฟล์ json (อยู่ข้างๆ ที่รันโปรแกรม)
    private static final Path DATA_DIR = Paths.get("data");

    // Gson ตัวเดียวใช้ร่วมกัน พร้อม adapter ของ java.time
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            // LocalDate <-> "2026-06-10"
            .registerTypeAdapter(LocalDate.class,
                    (com.google.gson.JsonSerializer<LocalDate>) (src, t, c) -> new JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalDate.class,
                    (com.google.gson.JsonDeserializer<LocalDate>) (json, t, c) -> LocalDate.parse(json.getAsString()))
            // LocalTime <-> "18:30"
            .registerTypeAdapter(LocalTime.class,
                    (com.google.gson.JsonSerializer<LocalTime>) (src, t, c) -> new JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalTime.class,
                    (com.google.gson.JsonDeserializer<LocalTime>) (json, t, c) -> LocalTime.parse(json.getAsString()))
            // LocalDateTime <-> "2026-06-10T18:30"
            .registerTypeAdapter(LocalDateTime.class,
                    (com.google.gson.JsonSerializer<LocalDateTime>) (src, t, c) -> new JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalDateTime.class,
                    (com.google.gson.JsonDeserializer<LocalDateTime>) (json, t, c) -> LocalDateTime.parse(json.getAsString()))
            .create();

    /** เซฟ object (List / Map / อะไรก็ได้) ลงไฟล์ json */
    public static void save(Object data, String fileName) {
        try {
            Files.createDirectories(DATA_DIR);
            String json = gson.toJson(data);
            Files.writeString(DATA_DIR.resolve(fileName), json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * โหลดข้อมูลกลับมา ต้องบอก type ที่แท้จริงผ่าน TypeToken เพราะ generic ถูกลบตอน runtime
     * ตัวอย่างการเรียก:
     *   List<Staff> list = JsonStorage.load("staff.json", new TypeToken<List<Staff>>(){}.getType());
     *   Map<String, Reservation> map = JsonStorage.load("reservations.json", new TypeToken<Map<String, Reservation>>(){}.getType());
     */
    public static <T> T load(String fileName, Type type) {
        Path file = DATA_DIR.resolve(fileName);
        if (!Files.exists(file)) {
            return null; // ยังไม่มีไฟล์ — ให้ service ตัดสินใจใช้ค่า default เอง
        }
        try {
            String json = Files.readString(file);
            return gson.fromJson(json, type);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
