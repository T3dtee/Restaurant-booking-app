module com.theerayut.app {
    requires javafx.controls;
    requires javafx.fxml;


    exports com.theerayut.app;
    exports com.theerayut.app.controller;
    exports com.theerayut.app.model;
    exports com.theerayut.app.service;
    exports com.theerayut.app.util;
    opens com.theerayut.app.controller to javafx.fxml;
}
