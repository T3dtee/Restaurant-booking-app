module com.theerayut.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;


    exports com.theerayut.app;
    exports com.theerayut.app.controller;
    exports com.theerayut.app.model;
    exports com.theerayut.app.service;
    exports com.theerayut.app.util;
    opens com.theerayut.app.controller to javafx.fxml;
    // เปิดให้ Gson เข้าถึง field ของ model ผ่าน reflection
    opens com.theerayut.app.model to com.google.gson;
}
