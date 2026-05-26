package com.theerayut.app.controller;

import com.theerayut.app.AppData;
import com.theerayut.app.model.StaffLogin;
import com.theerayut.app.service.Staff;
import com.theerayut.app.util.AnimationUtils;
import com.theerayut.app.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class loginStaffController {

    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private Label invalidText;
    @FXML
    private Button login;

    @FXML
    public void initialize() {
        //ถ้ามีข้อมูล login แล้ว ย้อนกลับมาหน้านี้ ให้ใส่ข้อมูลลงให้ช่องเติมข้อมูลเลย
        if (AppData.loginStaffData != null) {
            usernameField.setText(AppData.loginStaffData.getUsername());
            passwordField.setText(AppData.loginStaffData.getPassword());
        }

        AnimationUtils.buttonHover(login, 3, 100);
    }

    @FXML
    private void loginSubmit() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        boolean validUsername = !username.isEmpty();
        boolean validPassword = !password.isEmpty();

        usernameField.getStyleClass().remove("error-field");
        passwordField.getStyleClass().remove("error-field");

        Staff s = new Staff();

        if (validUsername && validPassword) {
            if (s.login(username, password)) {
                AppData.loginStaffData = new StaffLogin(username, password);
                goToStaffDashBoard();
            }
            else {
                usernameField.getStyleClass().add("error-field");
                passwordField.getStyleClass().add("error-field");
                invalidText.setVisible(true);
            }
        }
        else {
            if (!validUsername) usernameField.getStyleClass().add("error-field");
            if (!validPassword) passwordField.getStyleClass().add("error-field");
        }
    }

    @FXML
    private void goToUserLogin() {
        SceneManager.switchScene("login-user.fxml", "login.css");
    }
    @FXML
    private void goToStaffDashBoard() {
        SceneManager.switchScene("staffDashBoard.fxml", "staffDashBoard.css");
    }
}
