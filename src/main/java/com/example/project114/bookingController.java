package com.example.project114;

import com.example.project114.backend.Reservation;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalTime;

public class bookingController {

    @FXML
    private ChoiceBox<String> timeChoice;
    @FXML
    private Label name;
    @FXML
    private Label tel;
    @FXML
    private Label booked;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Button confirm;

    LocalTime time;

    @FXML
    public void initialize() {
        //date picker set today auto & can't choose past
        LocalDate today = LocalDate.now();
        LocalDate maxDate = today.plusDays(3);

        datePicker.setValue(LocalDate.now());
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                if (empty) return;

                if (date.isBefore(today) || date.isAfter(maxDate)) {
                    setDisable(true);  // กดไม่ได้
                    setStyle("-fx-background-color: #f0f0f0;");
                }
            }
        });
        datePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                setBookedText();
            }
        });

        name.setText(AppData.loginUserData.getName());
        tel.setText(AppData.loginUserData.getPhone());

        timeChoice.getItems().addAll(
                "10:00" ,"11:30" ,"13:00" ,"14:30" ,"16:00" ,"17:30" ,"19:00"
        );
        timeChoice.setValue(switchCaseTime(LocalTime.now()).toString());
        time = switchCaseTime(LocalTime.now());
        setBookedText();
        timeChoice.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    switch (newVal){
                        case "10:00" : time = LocalTime.of(10,0); break;
                        case "11:30" : time = LocalTime.of(11,30); break;
                        case "13:00" : time = LocalTime.of(13,0); break;
                        case "14:30" : time = LocalTime.of(14,30); break;
                        case "16:00" : time = LocalTime.of(16,0); break;
                        case "17:30" : time = LocalTime.of(17,30); break;
                        case "19:00" : time = LocalTime.of(19,0); break;
                    }
                    setBookedText();
                }
        );

        setupButtonAnimation(confirm);
    }

    private LocalTime switchCaseTime(LocalTime time){
        int gapTime = 90; //minute
        LocalTime openTime = LocalTime.of(10,0);
        for (int i = 0;i < 7;i++){
            if (time.isBefore(openTime.plusMinutes(gapTime * i))){
                return openTime.plusMinutes(gapTime * i);
            }
        }

        return openTime;
    }

    @FXML
    private Label guestNo;

    private byte guest = 1;

    @FXML
    private void onClickPlus() {
        if (guest < 6) {
            guest++;
            guestNo.setText(guest + " Guest");
        }
    }
    @FXML
    private void onClickReduce() {
        if (guest > 1) {
            guest--;
            guestNo.setText(guest + " Guest");
        }
    }
    @FXML
    private void confirmSubmit() {
        if (!AppData.allBookingData.isTableFull(datePicker.getValue(),time)) {
            AppData.bookingData = new Reservation(AppData.loginUserData, datePicker.getValue(), time, guest,
                    AppData.allBookingData.emptyTableNo(datePicker.getValue(),time));
            AppData.allBookingData.addReservation(AppData.bookingData);
            goToSuccess();
        }
    }
    @FXML
    private void setBookedText() {
        booked.setText(AppData.allBookingData.countByDateTime(datePicker.getValue(),time) + "/" + AppData.allBookingData.MAX_TABLES);
        if (AppData.allBookingData.isTableFull(datePicker.getValue(),time)) {
            confirm.getStyleClass().remove("confirm-btn");
            confirm.getStyleClass().add("table-full-btn");
        }
        else {
            confirm.getStyleClass().remove("table-full-btn");
            confirm.getStyleClass().add("confirm-btn");
        }
    }

    public void setupButtonAnimation(Button btn) {
        btn.setOnMouseEntered(e -> {
            if (AppData.allBookingData.isTableFull(datePicker.getValue(),time)) return;

            ScaleTransition st = new ScaleTransition(Duration.millis(100), btn);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();
        });

        btn.setOnMouseExited(e -> {
            if (AppData.allBookingData.isTableFull(datePicker.getValue(),time)) return;

            ScaleTransition st = new ScaleTransition(Duration.millis(100), btn);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });
    }

    @FXML
    private void goToUserLogin() { SceneManager.switchScene("login-user.fxml", "login.css");}

    @FXML
    private void goToSuccess() { SceneManager.switchScene("confrim-success.fxml", "success.css");}
}
