package com.theerayut.app.controller;

import com.theerayut.app.AppData;
import com.theerayut.app.model.Customer;
import com.theerayut.app.util.AnimationUtils;
import com.theerayut.app.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class loginUserController {

    @FXML
    private TextField telNumberField;
    @FXML
    private TextField nameUserField;
    @FXML
    private Label staffLogin;
    @FXML
    private Button login;

    @FXML
    public void initialize() {
        telNumberField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            // รับเฉพาะตัวเลข และไม่เกิน 10 ตัว
            if (newText.matches("\\d*") && newText.length() <= 10) {
                return change;
            }
            return null;
        }));

        //ถ้ามีข้อมูล login แล้ว ย้อนกลับมาหน้านี้ ให้ใส่ข้อมูลลงให้ช่องเติมข้อมูลเลย
        if (AppData.loginUserData != null){
            nameUserField.setText(AppData.loginUserData.getName());
            telNumberField.setText(AppData.loginUserData.getPhone());
        }
        if (AppData.loginStaffData != null){
            staffLogin.setVisible(true);
        }

        AnimationUtils.buttonHover(login, 3, 100);
    }

    @FXML
    private void loginSubmit() {
        String name = nameUserField.getText().trim();
        String tel  = telNumberField.getText().trim();

        boolean validName = name.length() > 2; // ชื่อต้องยาวตั้งแต่ 3 ตัวขึ้นไป
        boolean validTel  = tel.matches("\\d{10}");  // tel มีเลขครบ 10 ตัว

        // reset style
        nameUserField.getStyleClass().remove("error-field");
        telNumberField.getStyleClass().remove("error-field");

        if (validName && validTel) {
            if (AppData.loginUserData == null) {
                AppData.loginUserData = new Customer(name, tel);
            }
            else if (!name.equals(AppData.loginUserData.getName()) || !tel.equals(AppData.loginUserData.getPhone())){
                AppData.loginUserData = new Customer(name, tel);
            }
            goToBooking();
        } else {
            if (!validName) nameUserField.getStyleClass().add("error-field");
            if (!validTel)  telNumberField.getStyleClass().add("error-field");
        }
    }
    @FXML
    private void logoClick() {
        staffLogin.setVisible(true);
    }

    @FXML
    private void goToStaffLogin() {
        SceneManager.switchScene("login-staff.fxml", "login.css");
    }

    @FXML
    private void goToBooking() {SceneManager.switchScene("booking.fxml", "booking.css");}


}
