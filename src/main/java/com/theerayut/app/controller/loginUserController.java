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
            // 1. ถ้าเป็นการลบข้อความ (BackSpace / Delete) ให้ยอมให้ทำทันที ไม่ต้องคำนวณเพิ่ม
            if (change.isDeleted() && change.getText().isEmpty()) {
                int start = change.getRangeStart();
                String currentText = change.getControlText();

                // เช็คว่าตำแหน่งที่กำลังจะโดนลบ อยู่ข้างหลังเครื่องหมายขีดพอดีหรือไม่ (ตำแหน่งที่ 4 และ 8)
                if ((start == 4 || start == 8) && currentText.charAt(start - 1) == '-') {
                    // ขยายขอบเขตการลบ (Range) ให้กินพื้นที่ไปลบตัวเลขก่อนหน้าขีดด้วย 1 ตัว
                    change.setRange(start - 1, change.getRangeEnd());
                    return change;
                }
                return change;
            }

            String newText = change.getControlNewText();

            // 2. ดึงเอาเฉพาะตัวเลขออกมาเช็ค (คัดขีดออกไปก่อน) เพื่อตรวจสอบความยาว
            String digitsOnly = newText.replaceAll("-", "");

            // 3. ตรวจสอบว่าหลังจากคัดขีดออกแล้ว ต้องเป็นตัวเลขล้วน และยาวไม่เกิน 10 ตัว
            if (!digitsOnly.matches("\\d*") || digitsOnly.length() > 10) {
                return null; // ปฏิเสธการพิมพ์
            }

            // 4. ขั้นตอนการแปลงร่าง (Formatting Logic) ในขณะพิมพ์
            StringBuilder formatted = new StringBuilder();
            int digitCount = digitsOnly.length();

            // วางโครงสร้างตามแพทเทิร์น 000-000-0000
            if (digitCount > 0) {
                // ใส่ 3 ตัวแรก
                formatted.append(digitsOnly.substring(0, Math.min(digitCount, 3)));
            }
            if (digitCount > 3) {
                // เติมขีดแรก แล้วใส่ตัวที่ 4-6
                formatted.append("-").append(digitsOnly.substring(3, Math.min(digitCount, 6)));
            }
            if (digitCount > 6) {
                // เติมขีดที่สอง แล้วใส่ตัวที่ 7-10
                formatted.append("-").append(digitsOnly.substring(6, digitCount));
            }

            // 5. อัปเดตข้อความใหม่ลงไปในช่องกรอก และตั้งตำแหน่งเคอร์เซอร์ (Caret) ไว้ท้ายสุด
            change.setText(formatted.toString());
            change.setRange(0, change.getControlText().length());
            change.setCaretPosition(formatted.length());
            change.setAnchor(formatted.length());

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
