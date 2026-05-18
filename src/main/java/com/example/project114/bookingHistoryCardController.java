package com.example.project114;

import com.example.project114.backend.Reservation;
import com.example.project114.backend.ReservationStatus;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

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

    private Reservation reservation;

    public void setData(Reservation reservation) {
        this.reservation = reservation;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        String formatted = reservation.getDate().format(formatter);

        date.setText(formatted);
        time.setText(reservation.getTime().toString());
        table.setText("T" + reservation.getTableNo());
        guest.setText("" + reservation.getGuestCount());
        updateStatus();
    }

    private void updateStatus() {
        status.setText(reservation.getStatus().name());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
        if (reservation.getStatus() == ReservationStatus.BOOKED) {
            statusBar.setStyle("-fx-background-color: #41891C");
            statusTime.setText("Booking Time : " + reservation.getBookingTime().format(formatter));
        } else if (reservation.getStatus() == ReservationStatus.CHECKED_IN) {
            cancel.setVisible(false);
            statusBar.setStyle("-fx-background-color: #dcdc00");
            statusTime.setText("Check in Time : " + reservation.getCheckInTime().format(formatter));
        } else if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            cancel.setVisible(false);
            statusBar.setStyle("-fx-background-color: #b9b4b4");
            if (reservation.getCancelBy().getRole().equals("Staff")) {
                statusTime.setText("Cancel By Staff");
            } else if (reservation.getCancelBy().getRole().equals("Customer")) {
                statusTime.setText("Cancel By You");
            }
        }
    }

    private Runnable onCancelRequest;
    public void setOnCancelRequest(Runnable onCancelRequest) {
        this.onCancelRequest = onCancelRequest;
    }
    private Runnable onRemove;
    public void setOnRemove(Runnable onRemove) {
        this.onRemove = onRemove;
    }

    private void handleRemove() {
        if (onRemove != null) {
            onRemove.run();
        }
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
