package com.theerayut.app.util;

import javafx.animation.*;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.function.BooleanSupplier;

public class AnimationUtils {
    private static final Image BACK_IMAGE = new Image(AnimationUtils.class.getResourceAsStream("/com/example/app/style/img/triangle.png"));
    private static final Image HOME_IMAGE = new Image(AnimationUtils.class.getResourceAsStream("/com/example/app/style/img/home.png"));

    public static void popUpShow(AnchorPane mainContent, AnchorPane popUp, Pane blurPane, Node popUpBox, Node button) {
        Bounds buttonBounds = button.localToScene(button.getBoundsInLocal());
        double startX = buttonBounds.getMinX() + (buttonBounds.getWidth() / 2);
        double startY = buttonBounds.getMinY() + (buttonBounds.getHeight() / 2);

        double targetX = 0;
        double targetY = 0;

        popUpBox.setTranslateX(startX - (mainContent.getWidth() / 2));
        popUpBox.setTranslateY(startY - (mainContent.getHeight() / 2));

        Interpolator Curve_OUT = Interpolator.SPLINE(0.6, 1, 0.8, 1);

        TranslateTransition move = new TranslateTransition(Duration.millis(190), popUpBox);
        move.setToX(targetX);
        move.setToY(targetY);
        move.setInterpolator(Curve_OUT);

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
        popUpBox.setVisible(true);
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
            popUpBox.setVisible(false);
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

    public static void sideBarShow(AnchorPane mainContent, AnchorPane popUp, Pane blurPane,VBox sideBar) {
        sideBar.setTranslateX(-sideBar.getPrefWidth());
//        GaussianBlur blur = new GaussianBlur(0);
//        mainContent.setEffect(blur);

//        Timeline timelineBlur = new Timeline(
//                new KeyFrame(Duration.seconds(0.2),
//                        new KeyValue(blur.radiusProperty(), 15, Interpolator.EASE_OUT)
//                )
//        );

        FadeTransition overlayFade = new FadeTransition(Duration.seconds(0.27), blurPane);
        overlayFade.setFromValue(0.1);
        overlayFade.setToValue(0.9);
        overlayFade.setInterpolator(Interpolator.EASE_OUT);

        TranslateTransition sideBarTranslate = new TranslateTransition(Duration.millis(230), sideBar);
        sideBarTranslate.setFromX(-sideBar.getPrefWidth());
        sideBarTranslate.setToX(0);
        sideBarTranslate.setInterpolator(Interpolator.SPLINE(.35,.9,.2,1));

        Timeline t = new Timeline(
                new KeyFrame(Duration.millis(60),event -> {sideBarTranslate.play();})
        );

        popUp.setVisible(true);
        sideBar.setVisible(true);
        overlayFade.play();
        //timelineBlur.play();
        t.play();
    }

    public static void sideBarHide(AnchorPane mainContent, AnchorPane popUp, Pane blurPane,VBox sideBar) {
        //GaussianBlur blur = new GaussianBlur(15);
        //mainContent.setEffect(blur);

        FadeTransition overlayFade = new FadeTransition(Duration.seconds(0.16), blurPane);
        overlayFade.setFromValue(0.9);
        overlayFade.setToValue(0.1);

        TranslateTransition sideBarTranslate = new TranslateTransition(Duration.millis(160), sideBar);
        sideBarTranslate.setFromX(0);
        sideBarTranslate.setToX(-sideBar.getPrefWidth());
        sideBarTranslate.setInterpolator(Interpolator.EASE_IN);

//        Timeline timelineBlur = new Timeline(
//                new KeyFrame(Duration.seconds(0.18),
//                        new KeyValue(blur.radiusProperty(), 0, Interpolator.EASE_OUT)
//                )
//        );

        sideBarTranslate.setOnFinished(event -> {
            popUp.setVisible(false);
            sideBar.setVisible(false);
            mainContent.setEffect(null);
        });

        //timelineBlur.play();
        overlayFade.play();
        sideBarTranslate.play();
    }

    public static void buttonHover(Node btn, int size, int time) {
        buttonHover(btn, size, time, () -> true);
    }

    public static void buttonHover(Node btn, int size, int time, BooleanSupplier condition) {
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

    public static void cardRemove(Region regionItem, Runnable onFinished) {
        FadeTransition ft = new FadeTransition(Duration.millis(300), regionItem);
        ft.setToValue(0);

        Timeline collapse = new Timeline(
                new KeyFrame(Duration.millis(400),
                        new KeyValue(regionItem.prefHeightProperty(), 0, Interpolator.SPLINE(0.4, 1, 0.6, 1))
                )
        );

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, event -> ft.play()),
                new KeyFrame(Duration.millis(50), event -> collapse.play())
        );

        collapse.setOnFinished(event -> {
            if (onFinished != null) onFinished.run();
        });

        timeline.play();
    }

    public static void cardHide(Region regionItem) {
        FadeTransition ft = new FadeTransition(Duration.millis(300), regionItem);
        ft.setFromValue(100);
        ft.setToValue(0);

        Timeline collapse = new Timeline(
                new KeyFrame(Duration.millis(350),
                        new KeyValue(regionItem.prefHeightProperty(), 0, Interpolator.SPLINE(0.4, 0.4, 0.5, 1))
                )
        );

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, event -> ft.play()),
                new KeyFrame(Duration.millis(50), event -> collapse.play())
        );

        timeline.play();
    }

    public static void cardMoveIn(Region regionItem, double height) {
        regionItem.setTranslateX(-440);
        regionItem.setPrefHeight(0);
        regionItem.setOpacity(100);

        Timeline collapse = new Timeline(
                new KeyFrame(Duration.millis(300),
                        new KeyValue(regionItem.prefHeightProperty(), height, Interpolator.SPLINE(0.4, 0, 0.7, 1))
                )
        );

        TranslateTransition trans = new TranslateTransition(Duration.millis(300), regionItem);
        trans.setToX(0);
        trans.setInterpolator(Interpolator.SPLINE(0.4, 1, 0.6, 1));

        ScaleTransition s1 = new ScaleTransition(Duration.millis(120), regionItem);
        s1.setToX(0.94); s1.setToY(1.04);
        s1.setInterpolator(Interpolator.SPLINE(0.4, 0, 0.6, 1));

        ScaleTransition s2 = new ScaleTransition(Duration.millis(100), regionItem);
        s2.setToX(1.0); s2.setToY(1.0);
        s2.setInterpolator(Interpolator.SPLINE(0.4, 0, 0.6, 1));

        s1.setOnFinished(e -> s2.play());

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(50), event -> collapse.play()),
                new KeyFrame(Duration.millis(150), event -> trans.play()),
                new KeyFrame(Duration.millis(350), event -> s1.play())
        );

        timeline.play();
    }

    public static Image recolor(Image source, Color color) {
        int width  = (int) source.getWidth();
        int height = (int) source.getHeight();
        WritableImage result = new WritableImage(width, height);
        PixelReader reader = source.getPixelReader();
        PixelWriter writer = result.getPixelWriter();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color pixel = reader.getColor(x, y);
                writer.setColor(x, y, new Color(color.getRed(), color.getGreen(), color.getBlue(), pixel.getOpacity()));
            }
        }
        return result;
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
            st.stop();
            st2.stop();
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
            st.stop();
            st2.stop();
            st.play();
        });
    }
}
