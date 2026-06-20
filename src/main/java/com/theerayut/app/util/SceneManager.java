package com.theerayut.app.util;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SceneManager {

    private static Stage stage;
    private static StackPane mainContainer;
    private static boolean isTransitioning = false;
    private static Runnable afterTransitionCallback;

    public static void setAfterTransitionCallback(Runnable cb) {
        afterTransitionCallback = cb;
    }

    private static void fireAfterTransition() {
        if (afterTransitionCallback != null) {
            Runnable cb = afterTransitionCallback;
            afterTransitionCallback = null;
            cb.run();
        }
    }

    public enum TransitionType {
        FADE,
        SLIDE_IN,
        SLIDE_OUT
    }

    public static void setStage(Stage s) {
        stage = s;
        // สร้าง Container หลักรอไว้ครั้งแรกครั้งเดียว
        mainContainer = new StackPane();
        Scene scene = new Scene(mainContainer, 360, 800);
        stage.setScene(scene);
    }

    public static void switchSceneAsync(String fxml, TransitionType transition) {
        if (isTransitioning) return;
        isTransitioning = true;
        Thread t = new Thread(() -> {
            try {
                Parent nextRoot = FXMLLoader.load(
                        SceneManager.class.getResource("/com/example/app/ui/" + fxml)
                );
                Platform.runLater(() -> {
                    if (mainContainer.getChildren().isEmpty()) {
                        mainContainer.getChildren().add(nextRoot);
                        isTransitioning = false;
                        fireAfterTransition();
                    } else {
                        switch (transition) {
                            case FADE -> playFadeAnimation(nextRoot);
                            case SLIDE_IN -> playSlideInAnimation(nextRoot);
                            case SLIDE_OUT -> playSlideOutAnimation(nextRoot);
                        }
                        fireAfterTransition();
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> isTransitioning = false);
                e.printStackTrace();
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public static void switchScene(String fxml) {
        switchScene(fxml, TransitionType.FADE);
    }
    public static void switchScene(String fxml, TransitionType transition) {
        if (isTransitioning) return;
        try {
            Parent nextRoot = FXMLLoader.load(
                    SceneManager.class.getResource("/com/example/app/ui/" + fxml)
            );

            // ถ้าเป็นหน้าแรก (ไม่มีหน้าก่อนหน้า) ให้แสดงเลย
            if (mainContainer.getChildren().isEmpty()) {
                mainContainer.getChildren().add(nextRoot);
                Platform.runLater(SceneManager::fireAfterTransition);
            } else {
                isTransitioning = true;
                switch (transition) {
                    case FADE -> playFadeAnimation(nextRoot);
                    case SLIDE_IN -> playSlideInAnimation(nextRoot);
                    case SLIDE_OUT -> playSlideOutAnimation(nextRoot);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void playFadeAnimation(Parent nextScene) {
        if (mainContainer.getChildren().isEmpty()) {
            mainContainer.getChildren().add(nextScene);
            return;
        }
        nextScene.setOpacity(0);
        mainContainer.getChildren().add(nextScene);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), nextScene);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        fadeIn.setOnFinished(e -> {
            if (mainContainer.getChildren().size() > 1) {
                mainContainer.getChildren().removeFirst();
            }
            isTransitioning = false;
            fireAfterTransition();
        });
        fadeIn.play();
    }

    private static void playSlideInAnimation(Parent nextScene) {
        if (mainContainer.getChildren().isEmpty()) {
            mainContainer.getChildren().add(nextScene);
            return;
        }

        mainContainer.getChildren().add(nextScene);
        nextScene.setTranslateX(360);

        TranslateTransition moveOut = new TranslateTransition(Duration.millis(270), mainContainer.getChildren().getFirst());
        moveOut.setToX(-100);
        moveOut.setInterpolator(Interpolator.EASE_IN);

        TranslateTransition moveIn = new TranslateTransition(Duration.millis(270), nextScene);
        moveIn.setToX(0);
        moveIn.setInterpolator(Interpolator.SPLINE(0.3, 0.3, 0.6, 0.95));

        moveIn.setOnFinished(event -> {
            if (mainContainer.getChildren().size() > 1) {
                mainContainer.getChildren().removeFirst();
            }
            isTransitioning = false;
            fireAfterTransition();
        });

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, event ->  {
                    moveOut.play();
                }),
                new KeyFrame(Duration.millis(50), event -> {
                    moveIn.play();
                }));
        timeline.play();
    }
    private static void playSlideOutAnimation(Parent nextScene) {
        if (mainContainer.getChildren().isEmpty()) {
            mainContainer.getChildren().add(nextScene);
            return;
        }

        mainContainer.getChildren().addFirst(nextScene);
        nextScene.setTranslateX(-80);

        TranslateTransition moveOut = new TranslateTransition(Duration.millis(300), mainContainer.getChildren().getLast());
        moveOut.setToX(360);
        moveOut.setInterpolator(Interpolator.SPLINE(0.4, 0.05, 0.7, 0.7));

        TranslateTransition moveIn = new TranslateTransition(Duration.millis(300), nextScene);
        moveIn.setToX(0);
        moveIn.setInterpolator(Interpolator.SPLINE(0.3, 0.3, 0.6, 0.95));

        moveIn.setOnFinished(event -> {
            if (mainContainer.getChildren().size() > 1) {
                mainContainer.getChildren().removeLast();
            }
            isTransitioning = false;
            fireAfterTransition();
        });

        moveOut.play();
        moveIn.play();
    }
}
