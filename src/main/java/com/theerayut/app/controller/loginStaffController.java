package com.theerayut.app.controller;

import com.theerayut.app.AppData;
import com.theerayut.app.model.Person;
import com.theerayut.app.model.Staff;
import com.theerayut.app.service.StaffService;
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

        if (validUsername && validPassword) {
            switch (AppData.staffService.login(username, password)) {
                case Admin :
                    AppData.loginStaffData = new Staff(username, password, Person.Roles.Admin);
                    goToStaffDashBoard();
                    break;
                case Staff :
                    AppData.loginStaffData = new Staff(username, password, Person.Roles.Staff);
                    goToStaffDashBoard();
                    break;
                case null :
                    usernameField.getStyleClass().add("error-field");
                    passwordField.getStyleClass().add("error-field");
                    invalidText.setVisible(true);
                    break;
            }

        }
        else {
            if (!validUsername) usernameField.getStyleClass().add("error-field");
            if (!validPassword) passwordField.getStyleClass().add("error-field");
        }
    }

    @FXML
    private void goToUserLogin() {
        SceneManager.switchScene("login-user.fxml");
    }
    @FXML
    private void goToStaffDashBoard() {
        SceneManager.switchScene("staffDashBoard.fxml");
    }
}
