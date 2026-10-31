package com.theerayut.app.util;

import javafx.animation.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class AnimationUtils {

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
        popUpHide(mainContent, popUp, blurPane, popUpBox, null);
    }

    public static void popUpHide(AnchorPane mainContent, AnchorPane popUp, Pane blurPane,
                                 Node popUpBox, Runnable onHidden) {
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
            mainContent.setEffect(null);
            if (onHidden != null) onHidden.run();
        });

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
        // Keep the inner content at its real size so the collapse doesn't squash it.
        if (!regionItem.getChildrenUnmodifiable().isEmpty()
                && regionItem.getChildrenUnmodifiable().getFirst() instanceof Region inner) {
            inner.setMinHeight(inner.getHeight());
            inner.setPrefHeight(inner.getHeight());
        }

        // Clip to the card's shrinking bounds — the content is hidden as it rolls up, not resized.
        Rectangle clip = new Rectangle(regionItem.getWidth(), regionItem.getHeight());
        clip.widthProperty().bind(regionItem.widthProperty());
        clip.heightProperty().bind(regionItem.heightProperty());
        regionItem.setClip(clip);

        FadeTransition ft = new FadeTransition(Duration.millis(250), regionItem);
        ft.setFromValue(1);
        ft.setToValue(0);
        ft.setInterpolator(Interpolator.SPLINE(0.5, 0.8, 0.5, 1));

        Timeline collapse = new Timeline(
                new KeyFrame(Duration.millis(400),
                        new KeyValue(regionItem.prefHeightProperty(), 0, Interpolator.SPLINE(0.5, 0.8, 0.5, 1))
                )
        );

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(50), event -> collapse.play())
                ,new KeyFrame(Duration.millis(200), event -> ft.play())
        );

        timeline.play();
    }

    // Cascade tuning: each card starts STAGGER_STEP later than the one above it,
    // capped at STAGGER_MAX so a long list doesn't leave the last cards waiting.
    private static final double STAGGER_STEP = 45;
    private static final int STAGGER_MAX = 6;

    public static void cardMoveIn(Region regionItem, double height) {
        cardMoveInAt(regionItem, height, 0);
    }

    /** index = the card's position within its group (0, 1, 2, ...); drives the cascade. */
    public static void cardMoveInAt(Region regionItem, double height, int index) {
        // Collapse and hide immediately — before the stagger delay — so a queued
        // card takes up no space and stays invisible until its turn comes.
        regionItem.setPrefHeight(0);
        regionItem.setTranslateX(-180);
        regionItem.setOpacity(0);

        // One synchronized ease-in-out so the card eases in gently instead of
        // snapping, holds momentum through the travel, then settles softly.
        Interpolator ease = Interpolator.SPLINE(0.5, 0, 0.25, 1);
        Duration dur = Duration.millis(430);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(regionItem.prefHeightProperty(), 0),
                        new KeyValue(regionItem.translateXProperty(), -180),
                        new KeyValue(regionItem.opacityProperty(), 0)
                ),
                new KeyFrame(Duration.millis(280),
                        new KeyValue(regionItem.opacityProperty(), 1, Interpolator.EASE_OUT)
                ),
                new KeyFrame(dur,
                        new KeyValue(regionItem.prefHeightProperty(), height, ease),
                        new KeyValue(regionItem.translateXProperty(), 0, ease)
                )
        );

        timeline.setDelay(Duration.millis(Math.min(index, STAGGER_MAX) * STAGGER_STEP));
        timeline.play();
    }

    private static final Object HIGHLIGHT_ANIMATION_KEY = new Object();

    public static void cardHighlight(Region card) {
        cardHighlight(card, null, null);
    }

    public static void cardHighlight(Region card, Consumer<Runnable> beforeHighlight, Runnable onStarted) {
        cardHighlight(card, beforeHighlight, onStarted, null);
    }

    public static void cardHighlight(Region card, Consumer<Runnable> beforeHighlight,
                                     Runnable onStarted, Runnable onFinished) {
        Object previous = card.getProperties().get(HIGHLIGHT_ANIMATION_KEY);
        if (previous instanceof Animation animation) animation.stop();
        // Resolve CSS after the scene has had time to attach and process its ancestors.
        PauseTransition delay = new PauseTransition(Duration.millis(250));
        delay.setOnFinished(event -> {
            card.getProperties().remove(HIGHLIGHT_ANIMATION_KEY);
            if (card.getScene() == null) {
                if (onFinished != null) onFinished.run();
                return;
            }
            Runnable play = () -> {
                if (card.getScene() != null) playCardHighlight(card, onStarted, onFinished);
                else if (onFinished != null) onFinished.run();
            };
            if (beforeHighlight != null) beforeHighlight.accept(play);
            else play.run();
        });
        card.getProperties().put(HIGHLIGHT_ANIMATION_KEY, delay);
        delay.play();
    }

    private static void playCardHighlight(Region card, Runnable onStarted, Runnable onFinished) {
        card.setScaleX(1);
        card.setScaleY(1);

        // Resolve both borders from CSS so animation colors stay in the stylesheet.
        Node cssRoot = card.getScene().getRoot();
        cssRoot.applyCss();
        Border originalBorder = card.getBorder();
        card.getStyleClass().add("reservation-highlight");
        cssRoot.applyCss();
        Border highlightBorder = card.getBorder();
        card.getStyleClass().remove("reservation-highlight");
        cssRoot.applyCss();
        if (originalBorder == null || highlightBorder == null) {
            if (onFinished != null) onFinished.run();
            return;
        }
        card.setBorder(originalBorder);

        Interpolator ease = Interpolator.SPLINE(0.4, 0, 0.2, 1);
        Timeline pulse = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(card.borderProperty(), originalBorder),
                        new KeyValue(card.scaleXProperty(), 1),
                        new KeyValue(card.scaleYProperty(), 1)),
                new KeyFrame(Duration.millis(150),
                        new KeyValue(card.borderProperty(), highlightBorder, ease),
                        new KeyValue(card.scaleXProperty(), 1.03, ease),
                        new KeyValue(card.scaleYProperty(), 1.03, ease)),
                new KeyFrame(Duration.millis(400),
                        new KeyValue(card.borderProperty(), originalBorder, ease),
                        new KeyValue(card.scaleXProperty(), 1, ease),
                        new KeyValue(card.scaleYProperty(), 1, ease))
        );
        pulse.statusProperty().addListener((observable, oldStatus, newStatus) -> {
            if (newStatus == Animation.Status.STOPPED) {
                card.setBorder(originalBorder);
                card.setScaleX(1);
                card.setScaleY(1);
                card.getProperties().remove(HIGHLIGHT_ANIMATION_KEY);
                if (onFinished != null) onFinished.run();
            }
        });
        card.getProperties().put(HIGHLIGHT_ANIMATION_KEY, pulse);
        if (onStarted != null) onStarted.run();
        pulse.play();
    }

    public static void scrollThenCardMoveIn(ScrollPane scroll, Region card, double height, Runnable onFinished) {
        if (scroll.getScene() == null) return;
        scroll.getScene().getRoot().applyCss();
        scroll.getScene().getRoot().layout();
        if (card.getScene() != scroll.getScene()) return;
        card.setPrefHeight(0);
        card.setOpacity(0);
        PauseTransition waitForScene = new PauseTransition(Duration.millis(250));
        waitForScene.setOnFinished(ready -> {
            if (card.getScene() == null || card.getScene() != scroll.getScene()) return;
            scroll.getScene().getRoot().layout();
            Node content = scroll.getContent();
            double viewportHeight = scroll.getViewportBounds().getHeight();
            double range = Math.max(0, content.getLayoutBounds().getHeight() - viewportHeight);
            double valueRange = scroll.getVmax() - scroll.getVmin();
            double top = content.sceneToLocal(card.localToScene(card.getLayoutBounds())).getMinY()
                    - content.getLayoutBounds().getMinY();
            double finalRange = Math.max(0, content.getLayoutBounds().getHeight() + height - viewportHeight);
            // Center the expanded card where possible, without scrolling beyond either end.
            double desiredOffset = top + height / 2 - viewportHeight / 2;
            double finalOffset = Math.clamp(desiredOffset, 0, finalRange);
            double beforeOffset = Math.min(finalOffset, range);
            DoubleProperty offset = new SimpleDoubleProperty(beforeOffset);
            Runnable updateScroll = () -> {
                if (card.getScene() == null || card.getScene() != scroll.getScene()) return;
                scroll.getScene().getRoot().layout();
                double actualRange = Math.max(0, content.getLayoutBounds().getHeight()
                        - scroll.getViewportBounds().getHeight());
                scroll.setVvalue(actualRange > 0
                        ? scroll.getVmin() + Math.clamp(offset.get() / actualRange, 0, 1) * valueRange
                        : scroll.getVmin());
            };
            Interpolator ease = Interpolator.SPLINE(0.5, 0, 0.25, 1);
            double beforeValue = range > 0
                    ? scroll.getVmin() + beforeOffset / range * valueRange : scroll.getVmin();
            double currentOffset = valueRange > 0
                    ? (scroll.getVvalue() - scroll.getVmin()) / valueRange * range : 0;
            double scrollDistance = Math.abs(beforeOffset - currentOffset);
            double scrollMillis = Math.clamp(scrollDistance * 0.75, 200, 500);
            // Drive the actual scroll property directly, independently of the entrance's offset.
            Timeline approach = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(scroll.vvalueProperty(), scroll.getVvalue())),
                    new KeyFrame(Duration.millis(scrollMillis),
                            new KeyValue(scroll.vvalueProperty(), beforeValue, ease)));
            card.setTranslateX(-180);
            Timeline entrance = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(card.prefHeightProperty(), 0),
                            new KeyValue(offset, beforeOffset),
                            new KeyValue(card.opacityProperty(), 0),
                            new KeyValue(card.translateXProperty(), -180)),
                    new KeyFrame(Duration.millis(280),
                            new KeyValue(card.opacityProperty(), 1, Interpolator.EASE_OUT)),
                    new KeyFrame(Duration.millis(430),
                            new KeyValue(card.prefHeightProperty(), height, ease),
                            new KeyValue(offset, finalOffset, ease),
                            new KeyValue(card.translateXProperty(), 0,
                                    ease))
            );
            entrance.currentTimeProperty().addListener((observable, oldTime, newTime) -> updateScroll.run());
            SequentialTransition sequence = new SequentialTransition();
            if (scrollDistance > 2) sequence.getChildren().add(approach);
            else scroll.setVvalue(beforeValue);
            sequence.getChildren().add(new PauseTransition(Duration.millis(120)));
            sequence.getChildren().add(entrance);
            sequence.setOnFinished(event -> {
                updateScroll.run();
                if (card.getScene() != null && onFinished != null) onFinished.run();
            });
            sequence.play();
        });
        waitForScene.play();
    }

    public static void scrollToCard(ScrollPane scroll, Region card, Runnable onVisible) {
        if (scroll.getScene() == null) return;
        scroll.getScene().getRoot().applyCss();
        scroll.getScene().getRoot().layout();
        if (card.getScene() != scroll.getScene()) return;
        Node content = scroll.getContent();
        Bounds bounds = content.sceneToLocal(card.localToScene(card.getLayoutBounds()));
        double viewportHeight = scroll.getViewportBounds().getHeight();
        double contentMinY = content.getLayoutBounds().getMinY();
        double range = content.getLayoutBounds().getHeight() - viewportHeight;
        double valueRange = scroll.getVmax() - scroll.getVmin();
        double offset = range > 0 && valueRange > 0
                ? (scroll.getVvalue() - scroll.getVmin()) / valueRange * range : 0;
        double top = bounds.getMinY() - contentMinY;
        double bottom = bounds.getMaxY() - contentMinY;
        if (range <= 0 || valueRange <= 0
                || (top >= offset + 16 && bottom <= offset + viewportHeight - 16)) {
            if (onVisible != null) onVisible.run();
            return;
        }
        double targetOffset = Math.max(0, Math.min(range, (top + bottom - viewportHeight) / 2));
        double targetValue = scroll.getVmin() + targetOffset / range * valueRange;
        Timeline movement = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(scroll.vvalueProperty(), scroll.getVvalue())),
                new KeyFrame(Duration.millis(400),
                        new KeyValue(scroll.vvalueProperty(), targetValue,
                                Interpolator.SPLINE(0.4, 0, 0.2, 1))));
        movement.setOnFinished(event -> {
            if (card.getScene() != null && card.getScene() == scroll.getScene()
                    && onVisible != null) onVisible.run();
        });
        movement.play();
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

}
