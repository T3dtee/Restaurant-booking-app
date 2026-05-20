package com.example.project114;

import javafx.animation.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.function.BooleanSupplier;

public class AnimationUtils {
    public static void popUpShow(AnchorPane mainContent, AnchorPane popUp, Pane blurPane, Node popUpBox) {
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
            blurPane.setStyle(String.format(
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

    public static void popUpHide(AnchorPane mainContent, AnchorPane popUp, Pane blurPane, Node popUpBox) {
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
            blurPane.setStyle(String.format(
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

    public static void buttonHover(Button btn, int size, int time) {
        buttonHover(btn, size, time, () -> true);
    }

    public static void buttonHover(Button btn, int size, int time, BooleanSupplier condition) {
        btn.setOnMouseEntered(e -> {
            if (!condition.getAsBoolean()) return;

            ScaleTransition st = new ScaleTransition(Duration.millis(time), btn);
            st.setToX(1 + (double) size / 100);
            st.setToY(1 + (double) size / 100);
            st.play();
        });
        btn.setOnMouseExited(e -> {
            if (!condition.getAsBoolean()) return;

            ScaleTransition st = new ScaleTransition(Duration.millis(time), btn);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });
    }
}
