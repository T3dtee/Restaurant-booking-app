package com.theerayut.app;

import com.theerayut.app.util.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class main_application extends Application {
    @Override
    public void start(Stage stage) {
        stage.setTitle("Best Meal");
        SceneManager.setStage(stage);
        stage.setResizable(false);
        SceneManager.switchScene("login-user.fxml", "login.css");
        stage.show();
    }
}
