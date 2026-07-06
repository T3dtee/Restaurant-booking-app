package com.theerayut.app.controller;

import com.theerayut.app.AppData;
import com.theerayut.app.util.AnimationUtils;
import com.theerayut.app.util.SceneManager;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalTime;

public class bookingController {
    @FXML private ComboBox<LocalTime> timeChoice;
    @FXML private Label name;
    @FXML private Label tel;
    @FXML private VBox bookedBox;
    @FXML private Label booked;
    @FXML private DatePicker datePicker;
    @FXML private Button confirm;
    @FXML private Button history_btn;
    @FXML private AnchorPane mainContent;
    @FXML private AnchorPane popUp;
    @FXML private Pane blurOverlay;
    @FXML private VBox popUpBox;
    @FXML private VBox menuBtn;
    @FXML private VBox sideBar;
    @FXML private ImageView sbCloseIcon;
    @FXML private ImageView sbHomeIcon;
    @FXML private ImageView sbHistoryIcon;
    @FXML private ImageView sbLogoutIcon;
    @FXML private Label avatarInitial;
    @FXML private ImageView historyBtn;

    LocalTime time;

    @FXML
    public void initialize() {
        String n = AppData.loginUserData.getName();
        avatarInitial.setText(n.isEmpty() ? "?" : String.valueOf(n.charAt(0)).toUpperCase());

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

        AnimationUtils.buttonHover(menuBtn, 12, 110);
        AnimationUtils.buttonHover(confirm, 5, 100, () -> AppData.bookingService.timeSlotAvailable(datePicker.getValue(), time));

        Color gray = Color.web("#424242");
        historyBtn.setImage(AnimationUtils.recolor(historyBtn.getImage(), gray));
        sbCloseIcon.setImage(AnimationUtils.recolor(sbCloseIcon.getImage(), Color.web("#41891C")));
        sbHomeIcon.setImage(AnimationUtils.recolor(sbHomeIcon.getImage(), gray));
        sbHistoryIcon.setImage(AnimationUtils.recolor(sbHistoryIcon.getImage(), gray));
        sbLogoutIcon.setImage(AnimationUtils.recolor(sbLogoutIcon.getImage(), gray));
    }

    @FXML
    private void menuOnClick() {
        AnimationUtils.sideBarShow(mainContent, popUp, blurOverlay, sideBar);
    }
    @FXML
    private void hideSideBarOnClick() {
        AnimationUtils.sideBarHide(mainContent, popUp, blurOverlay, sideBar);
    }
    @FXML
    private void homeOnClick() {
        AnimationUtils.sideBarHide(mainContent, popUp, blurOverlay, sideBar);
    }
    @FXML
    private void historySideOnClick() {
        //AnimationUtils.sideBarHide(mainContent, popUp, blurOverlay, sideBar);
        goToHistory(SceneManager.TransitionType.SLIDE_LEFT);
    }
    @FXML
    private void logOutOnClick() {
        goToUserLogin();
    }

    @FXML
    private void historyOnClick() {
        goToHistory(SceneManager.TransitionType.SLIDE_IN);
    }

    @FXML
    private Label guestNo;

    private byte guest = 1;

    @FXML
    private void onClickPlus() {
        if (guest < AppData.config.getMaxGuest()) {
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
                AppData.bookingId = AppData.bookingService.book(date,time,AppData.loginUserData,guest).getReservationId();
                if (AppData.bookingId != null){
                    goToSuccess();
                }
            }
            else { //จองแล้ว
                showPopUp(confirm);
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
        goToHistory(SceneManager.TransitionType.SLIDE_IN);
    }

    private void setBookedText() {
        booked.setText(AppData.allBookingData.countByDateTime(datePicker.getValue(),time) + " / " + AppData.config.getMaxTables());
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

    private void showPopUp(Button button) {
        AnimationUtils.popUpShow(mainContent, popUp, blurOverlay, popUpBox, button);
    }
    private void hidePopUp() {
        AnimationUtils.popUpHide(mainContent, popUp, blurOverlay, popUpBox);
    }

    private void fullBookedAnimation() {
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

    private void goToUserLogin() { SceneManager.switchScene("login-user.fxml", SceneManager.TransitionType.FADE);}

    private void goToSuccess() { SceneManager.switchScene("success.fxml");}

    private void goToHistory(SceneManager.TransitionType type) {
        SceneManager.switchSceneAsync("bookingHistory.fxml", type);}
}
