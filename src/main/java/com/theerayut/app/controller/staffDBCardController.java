package com.theerayut.app.controller;

import com.theerayut.app.AppData;
import com.theerayut.app.model.Reservation;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.time.format.DateTimeFormatter;

public class staffDBCardController {
    @FXML
    private Label name;
    @FXML
    private Label phone;
    @FXML
    private Label date;
    @FXML
    private Label time;
    @FXML
    private Label guestNo;
    @FXML
    private Label table;
    @FXML
    private Button cancel;

    private Reservation reservation;

    public void setData(Reservation reservation) {
        this.reservation = reservation;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        String formatted = reservation.getDate().format(formatter);

        name.setText(reservation.getCustomer().getName());
        phone.setText("Phone : " + reservation.getCustomer().getPhone());
        date.setText("Date : " + formatted);
        time.setText("Time : " + reservation.getTime());
        guestNo.setText("Guest : " + reservation.getGuestCount());
        table.setText("Table : T" + reservation.getTableNo());
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

    private void handleRemove() {
        if (onRemove != null) {
            onRemove.run();
        }
    }

    @FXML
    private void checkedIn(){
        reservation.checkIn();
        handleRemove();
    }

    @FXML
    private void cancelled() {
        if (onCancelRequest != null) {
            onCancelRequest.run();
        }
    }

    public void confirmCancel() {
        reservation.cancel(AppData.loginStaffData);
        handleRemove();
    }
}
