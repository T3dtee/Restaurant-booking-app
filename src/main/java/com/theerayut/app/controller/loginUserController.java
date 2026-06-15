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
    private Label invalidText;

    @FXML
    public void initialize() {
        telNumberField.setTextFormatter(new TextFormatter<>(change -> {
            if (change.isDeleted() && change.getText().isEmpty()) {
                int s = change.getRangeStart();
                if ((s == 4 || s == 8) && change.getControlText().charAt(s - 1) == '-')
                    change.setRange(s - 1, change.getRangeEnd());
                return change;
            }

            String d = change.getControlNewText().replaceAll("-", "");
            if (!d.matches("\\d*") || d.length() > 10) return null;

            String f = d.length() > 6 ? d.substring(0, 3) + "-" + d.substring(3, 6) + "-" + d.substring(6)
                     : d.length() > 3 ? d.substring(0, 3) + "-" + d.substring(3)
                     : d;

            change.setText(f);
            change.setRange(0, change.getControlText().length());
            change.setCaretPosition(f.length());
            change.setAnchor(f.length());
            return change;
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
        String tel  = telNumberField.getText().trim().replace("-","");

        boolean validName = name.length() > 3; // ชื่อต้องยาวตั้งแต่ 4 ตัวขึ้นไป
        boolean validTel  = tel.matches("\\d{10}");  // tel มีเลขครบ 10 ตัว

        // reset style
        nameUserField.getStyleClass().remove("error-field");
        telNumberField.getStyleClass().remove("error-field");
        invalidText.setVisible(false);

        if (validName && validTel) {
            Customer c = AppData.loginService.customerLogin(name, tel);
            if (c == null){
                //ชื่อไม่ตรง id (เบอร์)
                nameUserField.getStyleClass().add("error-field");
                telNumberField.getStyleClass().add("error-field");
                invalidText.setVisible(true);
            } else {
                AppData.loginUserData = c;
                goToBooking(c.isFirstTimeLogin());
                c.setFirstTimeLogin(false);
            }

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
        SceneManager.switchScene("login-staff.fxml");
    }

    @FXML
    private void goToBooking(boolean firstTimeLogin) {
        if (firstTimeLogin) {
            SceneManager.switchScene("booking.fxml", SceneManager.TransitionType.FADE);
        }
        else {
            SceneManager.switchScene("booking.fxml", SceneManager.TransitionType.SLIDE_IN);
        }

    }
}
