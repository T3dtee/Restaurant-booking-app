package com.theerayut.app.controller;

import com.theerayut.app.AppData;
import com.theerayut.app.model.Reservation;
import com.theerayut.app.model.ReservationStatus;
import com.theerayut.app.util.AnimationUtils;
import com.theerayut.app.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class homeController {
    @FXML private AnchorPane mainContent;
    @FXML private AnchorPane popUp;
    @FXML private Pane blurOverlay;
    @FXML private VBox sideBar;
    @FXML private Button menuButton;
    @FXML private Button closeMenuButton;
    @FXML public Label welcome;
    @FXML private Label greeting;
    @FXML private VBox reservationSection;
    @FXML private VBox emptyCard;
    @FXML private Label cardHeading;
    @FXML private Label status;
    @FXML private Label when;
    @FXML private Label fullDate;
    @FXML private Label guests;
    @FXML private Label table;
    @FXML private ImageView sbCloseIcon;
    @FXML private ImageView sbHomeIcon;
    @FXML private ImageView sbDiningPlanIcon;
    @FXML private ImageView sbHistoryIcon;
    @FXML private ImageView sbLogoutIcon;

    private Reservation homeReservation;

    @FXML
    public void initialize() {
        LocalDateTime now = LocalDateTime.now();
        welcome.setText(AppData.loginUserData.isFirstTimeLogin()? "Welcome" : "Welcome back");
        greeting.setText("Hi, " + AppData.loginUserData.getName() + "!");
        homeReservation = AppData.allBookingData.getHomeReservation(AppData.loginUserData, now).orElse(null);
        boolean hasReservation = homeReservation != null;
        reservationSection.setVisible(hasReservation);
        reservationSection.setManaged(hasReservation);
        emptyCard.setVisible(!hasReservation);
        emptyCard.setManaged(!hasReservation);

        if (hasReservation) {
            boolean checkedIn = homeReservation.getStatus() == ReservationStatus.CHECKED_IN;
            cardHeading.setText(checkedIn ? "Current booking" : "Next booking");
            status.setText(checkedIn ? "Checked in" : "Booked");
            if (checkedIn) status.getStyleClass().add("home-status-checked-in");
            when.setText(formatWhen(LocalDateTime.of(homeReservation.getDate(), homeReservation.getTime()),
                    now.toLocalDate()));
            fullDate.setText(homeReservation.getDate().format(
                    DateTimeFormatter.ofPattern("EEEE, d MMM uuuu", Locale.ENGLISH)));
            int count = homeReservation.getGuestCount();
            guests.setText(count + (count == 1 ? " guest" : " guests"));
            table.setText("T" + homeReservation.getTableNo());
        }

        AnimationUtils.buttonHover(menuButton, 12, 110);

        sbCloseIcon.setImage(AnimationUtils.recolor(sbCloseIcon.getImage(), Color.web("#2e7d32")));
        sbHomeIcon.setImage(AnimationUtils.recolor(sbHomeIcon.getImage(), Color.web("#2e7d32")));
        sbDiningPlanIcon.setImage(AnimationUtils.recolor(sbDiningPlanIcon.getImage(), Color.web("#424242")));
        sbHistoryIcon.setImage(AnimationUtils.recolor(sbHistoryIcon.getImage(), Color.web("#424242")));
        sbLogoutIcon.setImage(AnimationUtils.recolor(sbLogoutIcon.getImage(), Color.web("#c93636")));
    }

    static String formatWhen(LocalDateTime start, LocalDate today) {
        String day;
        if (start.toLocalDate().equals(today)) day = "Today";
        else if (start.toLocalDate().equals(today.plusDays(1))) day = "Tomorrow";
        else day = start.format(DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH));
        return day + " at " + start.format(DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH));
    }

    @FXML
    private void bookOnClick() {
        SceneManager.switchScene("booking.fxml", SceneManager.TransitionType.FADE);
    }

    @FXML
    private void viewBookingOnClick() {
        if (homeReservation != null) AppData.bookingId = homeReservation.getReservationId();
        goToHistory();
    }

    @FXML
    private void historyOnClick() {
        goToHistory();
    }

    private void goToHistory() {
        AppData.bookingHistoryReturnPage = "home.fxml";
        SceneManager.switchSceneAsync("bookingHistory.fxml", SceneManager.TransitionType.SLIDE_LEFT);
    }

    @FXML
    private void diningPlanOnClick() {
        bookOnClick();
    }

    @FXML
    private void menuOnClick() {
        AnimationUtils.sideBarShow(mainContent, popUp, blurOverlay, sideBar);
        //closeMenuButton.requestFocus();
    }

    @FXML
    private void hideSideBarOnClick() {
        AnimationUtils.sideBarHide(mainContent, popUp, blurOverlay, sideBar);
        //menuButton.requestFocus();
    }

    @FXML
    private void logOutOnClick() {
        SceneManager.switchScene("login-user.fxml", SceneManager.TransitionType.FADE);
    }
}
