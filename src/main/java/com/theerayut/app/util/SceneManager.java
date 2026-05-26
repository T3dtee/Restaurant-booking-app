package com.theerayut.app.util;

import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SceneManager {

    private static Stage stage;
    private static StackPane mainContainer;

    public static void setStage(Stage s) {
        stage = s;
        // สร้าง Container หลักรอไว้ครั้งแรกครั้งเดียว
        mainContainer = new StackPane();
        Scene scene = new Scene(mainContainer, 360, 800);
        stage.setScene(scene);
    }

    public static void switchScene(String fxml, String css) {
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
                playFadeAnimation(nextRoot);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void playFadeAnimation(Parent nextScene) {
        // ตรวจสอบก่อนว่ามีหน้าเก่าอยู่จริงไหม (กัน Error .get(0))
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
                mainContainer.getChildren().remove(0); // ลบหน้าเก่าออกหลังจาก Fade เสร็จ
            }
        });
        fadeIn.play();
    }
}
