package com.theerayut.app.util;

import javafx.animation.*;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.function.BooleanSupplier;

public class AnimationUtils {
    private static final Image BACK_IMAGE = new Image(AnimationUtils.class.getResourceAsStream("/com/example/app/style/img/arrow.png"));
    private static final Image HOME_IMAGE = new Image(AnimationUtils.class.getResourceAsStream("/com/example/app/style/img/home.png"));

    public static void popUpShow(AnchorPane mainContent, AnchorPane popUp, Pane blurPane, Node popUpBox, Button button) {
        Bounds buttonBounds = button.localToScene(button.getBoundsInLocal());
        double startX = buttonBounds.getMinX() + (buttonBounds.getWidth() / 2);
        double startY = buttonBounds.getMinY() + (buttonBounds.getHeight() / 2);

        double targetX = 0;
        double targetY = 0;

        popUpBox.setTranslateX(startX - (mainContent.getWidth() / 2));
        popUpBox.setTranslateY(startY - (mainContent.getHeight() / 2));

        Interpolator SCurve_OUT = Interpolator.SPLINE(0.3, 0.2, 0.6, 0.95);

        TranslateTransition move = new TranslateTransition(Duration.millis(190), popUpBox);
        move.setToX(targetX);
        move.setToY(targetY);
        move.setInterpolator(SCurve_OUT);

        GaussianBlur blur = new GaussianBlur(0);
        mainContent.setEffect(blur);
        popUpBox.setOpacity(0);

        ScaleTransition s1 = new ScaleTransition(Duration.millis(150), popUpBox);
        s1.setFromX(0.6);
        s1.setFromY(0.6);
        s1.setToX(1.04);
        s1.setToY(1.04);
        s1.setInterpolator(Interpolator.EASE_OUT);

        ScaleTransition s2 = new ScaleTransition(Duration.millis(150), popUpBox);
        s2.setFromX(1.04);
        s2.setFromY(1.04);
        s2.setToX(1);
        s2.setToY(1);
        s2.setInterpolator(Interpolator.EASE_BOTH);

        ScaleTransition s3 = new ScaleTransition(Duration.millis(200), mainContent);
        s3.setFromX(1);
        s3.setFromY(1);
        s3.setToX(0.985);
        s3.setToY(0.985);
        s3.setInterpolator(Interpolator.EASE_BOTH);

        s1.setOnFinished(event -> s2.play());

        FadeTransition f = new FadeTransition(Duration.millis(150), popUpBox);
        f.setFromValue(0);
        f.setToValue(1);
        f.setInterpolator(Interpolator.EASE_OUT);

        Timeline t = new Timeline(
                new KeyFrame(Duration.seconds(0.2),
                        new KeyValue(blur.radiusProperty(), 15, Interpolator.EASE_OUT)
                )
        );

        FadeTransition overlayFade = new FadeTransition(Duration.seconds(0.3), blurPane);
        overlayFade.setFromValue(0.1);
        overlayFade.setToValue(0.9);
        overlayFade.setInterpolator(Interpolator.EASE_OUT);

        Timeline t1 = new Timeline(
                new KeyFrame(Duration.millis(60), event -> move.play())
        );

        Timeline t2 = new Timeline(
                new KeyFrame(Duration.millis(100),event -> {
                    s1.play();
                    f.play();
                    })
        );

        popUp.setVisible(true);
        t.play();
        overlayFade.play();
        t1.play();
        t2.play();
        s3.play();
    }

    public static void popUpHide(AnchorPane mainContent, AnchorPane popUp, Pane blurPane, Node popUpBox) {
        GaussianBlur blur = new GaussianBlur(15);
        mainContent.setEffect(blur);

        ScaleTransition s1 = new ScaleTransition(Duration.millis(150), popUpBox);
        s1.setFromX(1);
        s1.setFromY(1);
        s1.setToX(0.8);
        s1.setToY(0.8);

        FadeTransition f = new FadeTransition(Duration.millis(150), popUpBox);
        f.setFromValue(1);
        f.setToValue(0);
        f.setOnFinished(e -> {
            popUp.setVisible(false);
            mainContent.setEffect(null);});

        ScaleTransition s2 = new ScaleTransition(Duration.millis(200), mainContent);
        s2.setFromX(0.985);
        s2.setFromY(0.985);
        s2.setToX(1);
        s2.setToY(1);
        s2.setInterpolator(Interpolator.EASE_BOTH);

        Timeline t = new Timeline(
                new KeyFrame(Duration.seconds(0.18),
                        new KeyValue(blur.radiusProperty(), 0, Interpolator.EASE_OUT)
                )
        );

        Timeline t1 = new Timeline(
                new KeyFrame(Duration.millis(50),event -> {
                    s1.play();
                    f.play();
                })
        );

        FadeTransition overlayFade = new FadeTransition(Duration.seconds(0.2), blurPane);
        overlayFade.setFromValue(0.9);
        overlayFade.setToValue(0.1);

        t.play();
        t1.play();
        overlayFade.play();
        s2.play();
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
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();
        });
        btn.setOnMouseExited(e -> {
            if (!condition.getAsBoolean()) return;

            ScaleTransition st = new ScaleTransition(Duration.millis(time), btn);
            st.setToX(1.0);
            st.setToY(1.0);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();
        });
    }

    public static void backToHomeBtn(VBox backBtn, ImageView icon) {
        backBtn.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(100), icon);
            st.setToX(0);
            st.setToY(0);
            st.setInterpolator(Interpolator.EASE_IN);

            ScaleTransition st2 = new ScaleTransition(Duration.millis(100), icon);
            st2.setToX(1);
            st2.setToY(1);
            st2.setInterpolator(Interpolator.EASE_OUT);

            st.setOnFinished(event -> {
                icon.setImage(HOME_IMAGE);
                st2.play();
            });
            st.play();
        });

        backBtn.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(100), icon);
            st.setToX(0);
            st.setToY(0);
            st.setInterpolator(Interpolator.EASE_IN);

            ScaleTransition st2 = new ScaleTransition(Duration.millis(100), icon);
            st2.setToX(1);
            st2.setToY(1);
            st2.setInterpolator(Interpolator.EASE_OUT);

            st.setOnFinished(event -> {
                icon.setImage(BACK_IMAGE);
                st2.play();
            });
            st.play();
        });
    }
}
