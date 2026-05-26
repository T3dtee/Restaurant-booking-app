package com.theerayut.app.controller;

import com.theerayut.app.AppData;
import com.theerayut.app.util.SceneManager;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import java.util.Random;

import java.time.format.DateTimeFormatter;

public class successController {
    @FXML
    private Label successTitle;
    @FXML
    private Label name;
    @FXML
    private Label date;
    @FXML
    private Label time;
    @FXML
    private Label guestNo;
    @FXML
    private Label table;
    @FXML
    private StackPane container;
    @FXML
    private VBox detailBox;

    @FXML private Pane confettiPane; // Pane เปล่าๆ ที่ใช้วางพลุ
    private final Random random = new Random();

    @FXML
    public void initialize() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formatted = AppData.bookingData.getDate().format(formatter);
        name.setText("Name: " + AppData.bookingData.getCustomer().getName());
        date.setText("Date: " + formatted);
        time.setText("Time: " + AppData.bookingData.getTime());
        guestNo.setText("Guest: " + AppData.bookingData.getGuestCount());
        table.setText("Table: T" + AppData.bookingData.getTableNo());
        successTitle.setOpacity(0);
        detailBox.setOpacity(0);
        playSuccessEffect();
    }

    private void playSuccessEffect() {
        ScaleTransition bounce = new ScaleTransition(Duration.millis(360), container);
        bounce.setFromX(0);
        bounce.setFromY(0);
        bounce.setToX(1.6); // ขยายให้ใหญ่กว่าปกตินิดนึง
        bounce.setToY(1.6);
        bounce.setInterpolator(Interpolator.EASE_OUT);

        bounce.setOnFinished(e -> {
            ScaleTransition shrink = new ScaleTransition(Duration.millis(150), container);
            shrink.setToX(0.8);
            shrink.setToY(0.8);
            shrink.setInterpolator(Interpolator.EASE_BOTH);

            shrink.setOnFinished(a -> {
                ScaleTransition normal = new ScaleTransition(Duration.millis(110), container);
                normal.setToX(1.0);
                normal.setToY(1.0);
                normal.setInterpolator(Interpolator.EASE_BOTH);
                normal.play();
            });
            shrink.play();
        });
        bounce.play();
        playConfetti();

        FadeTransition fadeInTitle = new FadeTransition(Duration.millis(300), successTitle);
        fadeInTitle.setToValue(1);
        ParallelTransition titleAnim = new ParallelTransition(fadeInTitle);
        titleAnim.setDelay(Duration.millis(400));
        titleAnim.play();

        FadeTransition fadeInDetail = new FadeTransition(Duration.millis(300), detailBox);
        fadeInDetail.setToValue(1);
        ParallelTransition detailAnim = new ParallelTransition(fadeInDetail);
        detailAnim.setDelay(Duration.millis(500));
        detailAnim.play();
    }

    private void playConfetti() {
        int numberOfPieces = 70; // จำนวนชิ้นกระดาษ
        Color[] colors = {Color.web("#2ecc71"), Color.web("#3498db"), Color.web("#f1c40f"), Color.web("#e74c3c"), Color.web("#9b59b6")};

        for (int i = 0; i < numberOfPieces; i++) {
            Shape piece;
            if (random.nextBoolean()) {
                piece = new Circle(random.nextDouble() * 4 + 2);
            } else {
                piece = new Rectangle(random.nextDouble() * 6 + 2, random.nextDouble() * 6 + 2);
            }

            piece.setFill(colors[random.nextInt(colors.length)]);
            piece.setOpacity(random.nextDouble() * 0.5 + 0.5);
            piece.setTranslateX(random.nextDouble() * confettiPane.getPrefWidth());
            piece.setTranslateY(-100); // เริ่มต้นที่เหนือขอบบนของจอ 100 pixel

            confettiPane.getChildren().add(piece);

            TranslateTransition fall = new TranslateTransition(Duration.seconds(random.nextDouble() * 2 + 1), piece);
            fall.setToY(confettiPane.getPrefHeight() + 50); // เลื่อนลงไปจนพ้นขอบล่าง
            fall.setInterpolator(Interpolator.EASE_IN);

            RotateTransition rotate = new RotateTransition(Duration.seconds(random.nextDouble() * 1 + 0.5), piece);
            rotate.setByAngle(random.nextBoolean() ? 360 : -360); // หมุนครบรอบ
            rotate.setCycleCount(Animation.INDEFINITE); // หมุนไปเรื่อยๆ จนกว่าจะร่วงเสร็จ
            rotate.setInterpolator(Interpolator.LINEAR);

            fall.setOnFinished(e -> confettiPane.getChildren().remove(piece));
            fall.play();
            rotate.play();
        }
    }

    @FXML
    private void goToBooking() {SceneManager.switchScene("booking.fxml", "booking.css");}
}
