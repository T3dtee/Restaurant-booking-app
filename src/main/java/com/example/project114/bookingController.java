package com.example.project114;

import javafx.animation.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalTime;

public class bookingController {
    @FXML
    private ComboBox<LocalTime> timeChoice;
    @FXML
    private Label name;
    @FXML
    private Label tel;
    @FXML
    private VBox bookedBox;
    @FXML
    private Label booked;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Button confirm;
    @FXML
    private Button history_btn;

    @FXML
    private AnchorPane mainContent;
    @FXML
    private AnchorPane bookingPane;
    @FXML
    private AnchorPane popUp;
    @FXML
    private Pane blurOverlay;
    @FXML
    private VBox popUpBox;

    LocalTime time;

    @FXML
    public void initialize() {
        if (AppData.allBookingData.getReservationsByCustomer(AppData.loginUserData).isEmpty()) {
            history_btn.setDisable(true);
        }

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
    private void historyOnClick() {
        goToHistory();
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
        LocalDate date = datePicker.getValue();
        if (AppData.bookingService.timeSlotAvailable(datePicker.getValue(), time)) { //ว่าง
            if (!AppData.allBookingData.isCustomerBooked(AppData.loginUserData, datePicker.getValue(), time)) { //ยังไม่เคยจอง
                AppData.bookingData = AppData.bookingService.book(date,time,AppData.loginUserData,guest);
                if (AppData.bookingData != null){
                    goToSuccess();
                }
            }
            else { //จองแล้ว
                showPopUp();
            }
        }
        else { //ไม่ว่างแล้วพยายามกด
            if (AppData.allBookingData.isTableFull(date,time)) fullBookedAnimation();
        }
    }

    @FXML
    private void popUpCloseClick() {
        hidePopUp();
    }
    @FXML
    private void popUpBHClick() {
        hidePopUp();
        goToHistory();
    }

    private void setBookedText() {
        booked.setText(AppData.allBookingData.countByDateTime(datePicker.getValue(),time) + "/" + AppData.allBookingData.MAX_TABLES);
        confirm.getStyleClass().removeAll("confirm-btn", "table-full-btn");
        booked.getStyleClass().removeAll("booked-full");
        if (!AppData.bookingService.timeSlotAvailable(datePicker.getValue(),time)) {
            confirm.getStyleClass().add("table-full-btn");
            if (AppData.allBookingData.isTableFull(datePicker.getValue(),time)) {
                booked.getStyleClass().add("booked-full");
            }
        }
        else {
            confirm.getStyleClass().add("confirm-btn");
        }
    }

    private void showPopUp() {
        GaussianBlur blur = new GaussianBlur(0);
        mainContent.setEffect(blur);
        popUpBox.setOpacity(0);

        ScaleTransition s1 = new ScaleTransition(Duration.millis(80), popUpBox);
        s1.setFromX(0.9);
        s1.setFromY(0.9);
        s1.setToX(1);
        s1.setToY(1);
        s1.setInterpolator(Interpolator.EASE_OUT);

        FadeTransition f = new FadeTransition(Duration.millis(80), popUpBox);
        f.setFromValue(0);
        f.setToValue(1);
        f.setInterpolator(Interpolator.EASE_OUT);

        Timeline t = new Timeline(
                new KeyFrame(Duration.seconds(0.2),
                        new KeyValue(blur.radiusProperty(), 10)
                )
        );

        ObjectProperty<Color> color = new SimpleObjectProperty<>(
                Color.rgb(0, 0, 0, 0.0)
        );

        color.addListener((obs, oldV, newV) -> {
            blurOverlay.setStyle(String.format(
                    "-fx-background-color: rgba(%d,%d,%d,%.3f);",
                    (int)(newV.getRed() * 255),
                    (int)(newV.getGreen() * 255),
                    (int)(newV.getBlue() * 255),
                    newV.getOpacity()
            ));
        });

        Timeline t1 = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(color, Color.rgb(0,0,0,0.0))
                ),
                new KeyFrame(Duration.seconds(0.2),
                        new KeyValue(color, Color.rgb(0,0,0,0.15))
                )
        );
        Timeline t2 = new Timeline(
                new KeyFrame(Duration.millis(80),event -> {
                    s1.play();
                    f.play();})
        );

        popUp.setVisible(true);
        t.play();
        t1.play();
        t2.play();
    }

    private void hidePopUp() {
        GaussianBlur blur = new GaussianBlur(10);
        mainContent.setEffect(blur);

        ScaleTransition s1 = new ScaleTransition(Duration.millis(120), popUpBox);
        s1.setFromX(1);
        s1.setFromY(1);
        s1.setToX(0.8);
        s1.setToY(0.8);

        FadeTransition f = new FadeTransition(Duration.millis(120), popUpBox);
        f.setFromValue(1);
        f.setToValue(0);
        f.setOnFinished(e -> {
            popUp.setVisible(false);
            mainContent.setEffect(null);});

        Timeline t = new Timeline(
                new KeyFrame(Duration.seconds(0.12),
                        new KeyValue(blur.radiusProperty(), 0)
                )
        );

        ObjectProperty<Color> color = new SimpleObjectProperty<>(
                Color.rgb(0, 0, 0, 0.0)
        );
        color.addListener((obs, oldV, newV) -> {
            blurOverlay.setStyle(String.format(
                    "-fx-background-color: rgba(%d,%d,%d,%.3f);",
                    (int)(newV.getRed() * 255),
                    (int)(newV.getGreen() * 255),
                    (int)(newV.getBlue() * 255),
                    newV.getOpacity()
            ));
        });

        Timeline t1 = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(color, Color.rgb(0,0,0,0.15))
                ),
                new KeyFrame(Duration.seconds(0.12),
                        new KeyValue(color, Color.rgb(0,0,0,0.0))
                )
        );

        f.play();
        s1.play();
        t.play();
        t1.play();
    }

    public void fullBookedAnimation() {
        ScaleTransition s1 = new ScaleTransition(Duration.millis(150), bookedBox);
        s1.setToX(1.1);
        s1.setToY(1.1);
        s1.setInterpolator(Interpolator.EASE_BOTH);

        s1.setOnFinished(e -> {
            ScaleTransition s2 = new ScaleTransition(Duration.millis(170), bookedBox);
            s2.setToX(1);
            s2.setToY(1);
            s2.setInterpolator(Interpolator.EASE_BOTH);
            s2.play();
        });
        s1.play();
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
                            setStyle("-fx-text-fill: #e34646;");
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

    private void goToHistory() { SceneManager.switchScene("BookingHistory.fxml", "BookingHistory.css");}
}
