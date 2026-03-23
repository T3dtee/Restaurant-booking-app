module com.example.project114 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.project114 to javafx.fxml;
    exports com.example.project114;
    exports com.example.project114.backend;
    opens com.example.project114.backend to javafx.fxml;
}