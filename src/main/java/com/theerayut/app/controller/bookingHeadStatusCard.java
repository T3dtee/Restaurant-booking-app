package com.theerayut.app.controller;

import com.theerayut.app.model.Reservation;
import com.theerayut.app.model.ReservationStatus;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class bookingHeadStatusCard {
    @FXML private Label status;
    @FXML private VBox arrow;
    @FXML private HBox box;
    @FXML private Circle circle;
    @FXML private Label number;

    private enum State{
        SHOW,
        HIDE
    }
    private State state = State.SHOW;
    private Boolean inAnimation = false;
    private final List<Runnable> hideCard = new ArrayList<>();
    private final List<Runnable> showCard = new ArrayList<>();

    public void setData(ReservationStatus status, List<Reservation> reservations) {
        this.status.setText(status.toString());
        number.setText(String.valueOf(reservations.stream().filter(r -> r.getStatus() == status).count()));
        switch (status){
            case BOOKED -> {
                box.setStyle("-fx-background-color: #eaf3de;");
                circle.setStyle("-fx-fill: #3b6d11;");
                this.status.setStyle("-fx-text-fill: #3b6d11");
                number.setStyle("-fx-text-fill: #3b6d11");
            }
            case CHECKED_IN -> {
                box.setStyle("-fx-background-color: #ece4c6;");
                circle.setStyle("-fx-fill: #958200;");
                this.status.setStyle("-fx-text-fill: #958200");
                number.setStyle("-fx-text-fill: #958200");
            }
            case CANCELLED -> {
                box.setStyle("-fx-background-color: #e4e4e4;");
                circle.setStyle("-fx-fill: #5c5c5c;");
                this.status.setStyle("-fx-text-fill: #5c5c5c");
                number.setStyle("-fx-text-fill: #5c5c5c");
            }
            case EXPIRED -> {
                box.setStyle("-fx-background-color: #e2eafd;");
                circle.setStyle("-fx-fill: #4f5d80;");
                this.status.setStyle("-fx-text-fill: #4f5d80");
                number.setStyle("-fx-text-fill: #4f5d80");
            }
        }
    }

    public void collapseInitially(){
        state = State.HIDE;
        arrow.setRotate(-180);
    }

    public void addHideCard(Runnable r){
        hideCard.add(r);
    }
    public void addShowCard(Runnable r){
        showCard.add(r);
    }

    @FXML
    private void onClick(){
        if (inAnimation == false){
            inAnimation = true;
            switch (state){
                case SHOW -> hide();
                case HIDE -> show();
            }
        }

    }

    private void hide(){
        RotateTransition rotateTransition = new RotateTransition(Duration.millis(250), arrow);
        rotateTransition.setByAngle(0);
        rotateTransition.setToAngle(-180);
        rotateTransition.setInterpolator(Interpolator.SPLINE(0.5,0.1,0.5,1));
        rotateTransition.play();
        for (Runnable r : hideCard) {r.run();}
        state = State.HIDE;
        Timeline t = new Timeline(
                new KeyFrame(Duration.millis(400))
        );
        t.setOnFinished(_ -> inAnimation = false);
        t.play();
    }
    private void show(){
        RotateTransition rotateTransition = new RotateTransition(Duration.millis(250), arrow);
        rotateTransition.setByAngle(-180);
        rotateTransition.setToAngle(0);
        rotateTransition.setInterpolator(Interpolator.SPLINE(0.5,0.1,0.5,1));
        rotateTransition.play();
        for (Runnable r : showCard) {r.run();}
        state = State.SHOW;
        Timeline t = new Timeline(
                new KeyFrame(Duration.millis(400))
        );
        t.setOnFinished(_ -> inAnimation = false);
        t.play();
    }
}
