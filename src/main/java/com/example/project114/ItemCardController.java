package com.example.project114;

import com.example.project114.backend.Reservation;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.time.format.DateTimeFormatter;
import java.util.function.BooleanSupplier;

public class ItemCardController {
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

    private Reservation data;

    public void setData(Reservation data) {
        this.data = data;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        String formatted = data.getDate().format(formatter);

        name.setText(data.getCustomer().getName());
        phone.setText("Phone : " + data.getCustomer().getPhone());
        date.setText("Date : " + formatted);
        time.setText("Time : " + data.getTime());
        guestNo.setText("Guest : " + data.getGuestCount());
        table.setText("Table : T" + data.getQueueNumber());
    }

    private Runnable onRemove;
    public void setOnRemove(Runnable onRemove) {
        this.onRemove = onRemove;
    }
    private BooleanSupplier onCancelRequest;
    public void setOnCancelRequest(BooleanSupplier onCancelRequest) {
        this.onCancelRequest = onCancelRequest;
    }

    private void handleRemove() {
        if (onRemove != null) {
            onRemove.run();
        }
    }

    @FXML
    private void checkedIn(){
        data.checkIn();
        handleRemove();
    }
    @FXML
    private void cancelled(){
        if (onCancelRequest != null && !onCancelRequest.getAsBoolean()) {
            return;
        }
        data.cancel();
        handleRemove();
    }
}
