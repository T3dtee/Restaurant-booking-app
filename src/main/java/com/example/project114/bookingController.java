package com.example.project114;

import com.example.project114.backend.Reservation;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class bookingController {
    @FXML
    private ComboBox<String> timeChoice;
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

    //config time
    int gapTime = 90; //minute
    LocalTime openTime = LocalTime.of(10,0);
    LocalTime closeTime = LocalTime.of(20,30);
    int timeIndex = 0; //do not touch

    @FXML
    public void initialize() {
        //หาจำนวนช่วงเวลา
        while (closeTime.isAfter(openTime.plusMinutes(gapTime * timeIndex))){
            timeIndex++;
        }
        String []timeList = new String[timeIndex];
        //คำนวนเวลาแต่ละช่วง
        for (int i = 0; i < timeIndex; i++){
            timeList[i] = openTime.plusMinutes(gapTime * i).toString();
        }

        //เลือกวันนี้ให้ auto ถ้าเลยเวลาร้านปิดไปแล้วเลือกวันถัดไป
        LocalDate canBookingDay;
        if (LocalTime.now().isAfter(closeTime)){
            canBookingDay = LocalDate.now().plusDays(1);
        }
        else {
            canBookingDay = LocalDate.now();
        }
        //config จองล่วงหน้าได้กี่วัน
        LocalDate maxDate = canBookingDay.plusDays(3);

        datePicker.setValue(canBookingDay);
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                if (empty) return;

                if (date.isBefore(canBookingDay) || date.isAfter(maxDate)) {
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

        timeChoice.getItems().addAll(timeList);  //ใส่ตัวเลือกช่วงเวลา
        timeChoice.setValue(timeForBooking(LocalTime.now()).toString());
        time = timeForBooking(LocalTime.now());
        setBookedText();
        timeChoice.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    time = LocalTime.parse(newVal);
                    setBookedText();
                }
        );
        setTimeChoice();

        setupButtonAnimation(confirm);
    }

    private LocalTime timeForBooking(LocalTime time){ //หาช่วงเวลาที่จองได้
        for (int i = 0;i < timeIndex;i++){
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

    private void setTimeChoice(){
        timeChoice.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setDisable(false);
                } else {
                    setText(item);
                    if (LocalDateTime.of(datePicker.getValue(),LocalTime.parse(item)).isBefore(LocalDateTime.now())) {
                        setDisable(true);
                        setStyle("-fx-text-fill: #989898;");
                    } else {
                        setDisable(false);
                    }
                }
            }
        });
    }

    private void setupButtonAnimation(Button btn) {
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
