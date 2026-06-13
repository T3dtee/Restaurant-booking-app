package com.theerayut.app.controller;

import com.theerayut.app.AppData;
import com.theerayut.app.model.Person;
import com.theerayut.app.model.Reservation;
import com.theerayut.app.model.ReservationStatus;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;

public class bookingHistoryCardController {
    @FXML
    private Pane statusBar;
    @FXML
    private Label status;
    @FXML
    private Label date;
    @FXML
    private Label time;
    @FXML
    private Label table;
    @FXML
    private Label guest;
    @FXML
    private Label statusTime;
    @FXML
    private Button cancel;
    @FXML
    private Label bookingId;
    @FXML
    private VBox statusBox;

    private Reservation reservation;

    public void setData(Reservation reservation) {
        this.reservation = reservation;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        String formatted = reservation.getDate().format(formatter);

        bookingId.setText("#" + reservation.getReservationId());
        date.setText(formatted);
        time.setText(reservation.getTime().toString());
        table.setText("T" + reservation.getTableNo());
        guest.setText("" + reservation.getGuestCount());
        updateStatus();
    }

    private void updateStatus() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
        if (reservation.getStatus() == ReservationStatus.BOOKED) {
            status.setText("Booked");
            status.setStyle("-fx-text-fill: #3b6d11;");
            statusBox.setStyle("-fx-background-color: #eaf3de");
            statusBar.setStyle("-fx-background-color: #41891C");
            statusTime.setText("Booking Time : " + reservation.getBookingTime().format(formatter));
        } else if (reservation.getStatus() == ReservationStatus.CHECKED_IN) {
            status.setText("Checked In");
            status.setStyle("-fx-text-fill: #958200;");
            statusBox.setStyle("-fx-background-color: #ece4c6");
            cancel.setVisible(false);
            statusBar.setStyle("-fx-background-color: #e1e100");
            statusTime.setText("Check in Time : " + reservation.getCheckInTime().format(formatter));
        } else if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            status.setText("Cancelled");
            status.setStyle("-fx-text-fill: #5c5c5c;");
            statusBox.setStyle("-fx-background-color: #e4e4e4");
            cancel.setVisible(false);
            statusBar.setStyle("-fx-background-color: #b9b4b4");
            if (reservation.getCancelByRole() == Person.Roles.Staff || reservation.getCancelByRole() == Person.Roles.Admin) {
                statusTime.setText("Cancel By Staff");
            } else if (reservation.getCancelByRole() == Person.Roles.Customer) {
                statusTime.setText("Cancel By You");
            } else statusTime.setText("Not Check In");

        } else if (reservation.getStatus() == ReservationStatus.EXPIRED) {
            status.setText("Expired");
            status.setStyle("-fx-text-fill: #4f5d80;");
            statusBox.setStyle("-fx-background-color: #e2eafd");
            cancel.setVisible(false);
            statusBar.setStyle("-fx-background-color: #8b97b6");
            statusTime.setText("Completed / Past Booking");
        }
    }

    public Button getCancelBtn() {
        return cancel;
    }

    private Runnable onCancelRequest;
    public void setOnCancelRequest(Runnable onCancelRequest) {
        this.onCancelRequest = onCancelRequest;
    }
    private Runnable onRemove;
    public void setOnRemove(Runnable onRemove) {
        this.onRemove = onRemove;
    }
    private Runnable onHide;
    public void setOnHide(Runnable onHide) {
        this.onHide = onHide;
    }

    private Runnable onAdd;
    public void setOnAdd(Runnable onAdd) {
        this.onAdd = onAdd;
    }
    public void moveInAction() {
        if (reservation.isDoneBooking()){
            reservation.setDoneBooking(false);
            onAdd.run();
        }
    }
    public void showCard(){
        if (onAdd != null) {
            onAdd.run();
        }
    }

    public void handleRemove() {
        if (onRemove != null) onRemove.run();
    }

    public void handleHide() {
        if (onHide != null) onHide.run();
    }

    public void confirmCancel() {
        reservation.cancel(AppData.loginUserData);
        updateStatus();
    }

    @FXML
    private void cancelOnClick() {
        if (onCancelRequest != null) {
            onCancelRequest.run();
        }
    }
}
