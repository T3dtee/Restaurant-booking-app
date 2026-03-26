package com.example.project114;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class bookingController {
    @FXML
    private ComboBox<LocalTime> timeChoice;
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
        datePicker.setValue(AppData.bookingService.getCanBookingDate());
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                if (empty) return;

                if (date.isBefore(AppData.bookingService.getCanBookingDate()) || date.isAfter(AppData.bookingService.getMaxBookingDate())) {
                    setDisable(true);  // กดไม่ได้
                    setStyle("-fx-background-color: #f0f0f0;");
                }
            }
        });
        datePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                setBookedText();
                setTimeChoice();
            }
        });

        name.setText(AppData.loginUserData.getName());
        tel.setText(AppData.loginUserData.getPhone());

        timeChoice.getItems().addAll(AppData.bookingService.getTimeSlotList());  //ใส่ตัวเลือกช่วงเวลา
        timeChoice.setValue(AppData.bookingService.canBookingTime(AppData.bookingService.getCanBookingDate(), LocalTime.now()));
        time = AppData.bookingService.canBookingTime(AppData.bookingService.getCanBookingDate(), LocalTime.now());
        setBookedText();
        timeChoice.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    time = newVal;
                    setTimeChoice();
                    setBookedText();
                }
        );
        setTimeChoice();

        setupButtonAnimation(confirm);
    }

    @FXML
    private Label guestNo;

    private byte guest = 1;

    @FXML
    private void onClickPlus() {
        if (guest < AppData.bookingService.maxGuest) {
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
        AppData.bookingData = AppData.bookingService.book(datePicker.getValue(),time,AppData.loginUserData,guest);
        if (AppData.bookingData != null){
            goToSuccess();
        }
    }
    @FXML
    private void setBookedText() {
        booked.setText(AppData.allBookingData.countByDateTime(datePicker.getValue(),time) + "/" + AppData.allBookingData.MAX_TABLES);
        confirm.getStyleClass().removeAll("confirm-btn", "table-full-btn");
        if (!AppData.bookingService.timeSlotAvailable(datePicker.getValue(),time)) {
            confirm.getStyleClass().add("table-full-btn");
        }
        else {
            confirm.getStyleClass().add("confirm-btn");
        }
    }

    private void setTimeChoice(){
        timeChoice.setCellFactory(lv -> new ListCell<LocalTime>() {
            @Override
            protected void updateItem(LocalTime item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setDisable(false);
                } else {
                    setText(item.toString());
                    if (!AppData.bookingService.timeSlotAvailable(datePicker.getValue(), item)) {
                        if (AppData.allBookingData.isTableFull(datePicker.getValue(), item)) {
                            setStyle("-fx-text-fill: #d86161;");
                            setDisable(false);
                        }
                        else { //เลยเวลา
                            setDisable(true);
                            setStyle("-fx-text-fill:  #cfcfcf;");
                        }

                    } else {
                        setDisable(false);
                    }
                }
            }
        });
    }

    private void setupButtonAnimation(Button btn) {
        btn.setOnMouseEntered(e -> {
            if (!AppData.bookingService.timeSlotAvailable(datePicker.getValue(),time)) return;

            ScaleTransition st = new ScaleTransition(Duration.millis(100), btn);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();
        });

        btn.setOnMouseExited(e -> {
            if (!AppData.bookingService.timeSlotAvailable(datePicker.getValue(),time)) return;

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
