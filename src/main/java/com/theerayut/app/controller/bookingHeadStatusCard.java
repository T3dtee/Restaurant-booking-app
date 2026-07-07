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
    @FXML private VBox numberBox;

    // status style prefix, e.g. "booked" -> .booked-box / .booked-dot / .booked-text
    private String styleKey = "";

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
        styleKey = switch (status){
            case BOOKED -> "booked";
            case CHECKED_IN -> "checked-in";
            case CANCELLED -> "cancelled";
            case EXPIRED -> "expired";
        };
        // dot + text colours stay constant regardless of collapse state
        circle.getStyleClass().add(styleKey + "-dot");
        this.status.getStyleClass().add(styleKey + "-text");
        number.getStyleClass().add(styleKey + "-text");
        applyStateStyle();
    }

    // Collapsed: plain white card with a hairline border and a grey number chip.
    // Expanded: the status colour set by setData.
    private void applyStateStyle(){
        box.getStyleClass().removeAll(styleKey + "-box", "collapsed-box");
        numberBox.getStyleClass().remove("collapsed-number-box");
        if (state == State.SHOW){
            box.getStyleClass().add(styleKey + "-box");
        } else {
            box.getStyleClass().add("collapsed-box");
            numberBox.getStyleClass().add("collapsed-number-box");
        }
    }

    public void collapseInitially(){
        state = State.HIDE;
        arrow.setRotate(-180);
        applyStateStyle();
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
        applyStateStyle();
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
        applyStateStyle();
        Timeline t = new Timeline(
                new KeyFrame(Duration.millis(400))
        );
        t.setOnFinished(_ -> inAnimation = false);
        t.play();
    }
}
