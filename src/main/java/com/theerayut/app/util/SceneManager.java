package com.theerayut.app.util;

import javafx.animation.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SceneManager {

    private static Stage stage;
    private static StackPane mainContainer;

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

    public static void switchScene(String fxml, String css) {
        switchScene(fxml, css, TransitionType.FADE);
    }
    public static void switchScene(String fxml, String css, TransitionType transition) {
        try {
            Parent nextRoot = FXMLLoader.load(
                    SceneManager.class.getResource("/com/example/app/ui/" + fxml)
            );

            if (css != null && !css.isEmpty()) {
                nextRoot.getStylesheets().add(
                        SceneManager.class.getResource("/com/example/app/style/" + css)
                                .toExternalForm()
                );
            }
            // ถ้าเป็นหน้าแรก (ไม่มีหน้าก่อนหน้า) ให้แสดงเลย
            if (mainContainer.getChildren().isEmpty()) {
                mainContainer.getChildren().add(nextRoot);
            } else {
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
                mainContainer.getChildren().removeFirst(); // ลบหน้าเก่าออกหลังจาก Fade เสร็จ
            }
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
        moveOut.setInterpolator(Interpolator.SPLINE(0.4, 0.1, 0.7, 0.7));

        TranslateTransition moveIn = new TranslateTransition(Duration.millis(270), nextScene);
        moveIn.setToX(0);
        moveIn.setInterpolator(Interpolator.SPLINE(0.3, 0.3, 0.6, 0.95));

        moveIn.setOnFinished(event -> {
            if (mainContainer.getChildren().size() > 1) {
                mainContainer.getChildren().removeFirst();
            }
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
        });

        moveOut.play();
        moveIn.play();
    }
}
