package com.theerayut.app.controller;

import com.theerayut.app.AppData;
import com.theerayut.app.model.Person;
import com.theerayut.app.model.Reservation;
import com.theerayut.app.model.ReservationStatus;
import com.theerayut.app.model.RestaurantConfig;
import com.theerayut.app.model.Staff;
import com.theerayut.app.service.StaffService;
import javafx.collections.ObservableList;
import com.theerayut.app.util.AnimationUtils;
import com.theerayut.app.util.SceneManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class adminController {
    @FXML private Label todayBook;
    @FXML private Label checkedIn;
    @FXML private Label todayCancel;
    @FXML private TextField totalTableField;
    @FXML private TextField maxGuestField;
    @FXML private TextField gapTimeField;
    @FXML private TextField openTimeField;
    @FXML private TextField closeTimeField;
    @FXML private TextField maxAdvanceBooking;
    @FXML private VBox table_save;
    @FXML private VBox time_save;
    @FXML private TableView<Staff> staffTable;
    @FXML private TableColumn<Staff, String> usernameCol;
    @FXML private TableColumn<Staff, String> roleCol;
    @FXML private TableColumn<Staff, Void> actionCol;
    @FXML private AnchorPane mainContent;
    @FXML private AnchorPane popUpPane;
    @FXML private Pane blurOverlay;
    @FXML private VBox popUpBox;
    @FXML private TextField usernameField;
    @FXML private TextField passwordField;
    @FXML private ComboBox<StaffService.Roles> roleSelect;
    @FXML private VBox addStaff;
    @FXML private Label formTitle;

    private final ObservableList<Staff> staffItems = FXCollections.observableArrayList();
    private Staff editingStaff; // null = โหมดเพิ่ม, ไม่ null = โหมดแก้ไข

    @FXML private void formCancelOnClick() {
        resetForm();
        hidePopUp();
    }

    @FXML private void formSaveOnClick() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        StaffService.Roles role = roleSelect.getValue();

        // validation — กรอกครบทุกช่อง
        boolean valid = true;
        if (username.isEmpty()) { markInvalid(usernameField); valid = false; }
        if (password.isEmpty()) { markInvalid(passwordField); valid = false; }
        if (role == null) { roleSelect.setStyle("-fx-border-color: red;"); valid = false; }
        if (!valid) return;

        // กัน username ซ้ำ (ข้ามตัวที่กำลังแก้ไขอยู่)
        boolean duplicate = AppData.staffService.getStaffList().stream()
                .anyMatch(s -> s != editingStaff && s.getUsername().equalsIgnoreCase(username));
        if (duplicate) {
            markInvalid(usernameField);
            return;
        }

        // StaffService.Roles -> Person.Roles (ชื่อ enum ตรงกัน: Admin/Staff)
        Staff staff = new Staff(username, password, Person.Roles.valueOf(role.name()));

        if (editingStaff != null) {
            // โหมดแก้ไข — แทนที่ของเดิม คงตำแหน่งเดิมในตาราง
            int idx = staffItems.indexOf(editingStaff);
            AppData.staffService.removeStaff(editingStaff);
            AppData.staffService.addStaff(staff);
            if (idx >= 0) staffItems.set(idx, staff);
        } else {
            // โหมดเพิ่ม
            AppData.staffService.addStaff(staff);
            staffItems.add(staff);
        }

        resetForm();
        hidePopUp();
    }

    private void markInvalid(TextField field) {
        field.setStyle("-fx-border-color: red;");
    }

    private void resetForm() {
        editingStaff = null;
        usernameField.clear();
        passwordField.clear();
        roleSelect.getSelectionModel().clearSelection();
        usernameField.setStyle("");
        passwordField.setStyle("");
        roleSelect.setStyle("");
    }

    @FXML private void addStaff() {
        editingStaff = null;
        resetForm();
        formTitle.setText("Add Staff");
        showPopUp(addStaff);
    }

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    public void initialize() {
        // --- stats ---
        List<Reservation> todayList = AppData.allBookingData.getReservationsByDate(LocalDate.now());

        todayBook.setText(String.valueOf(todayList.stream()
                .filter(r -> r.getStatus() == ReservationStatus.BOOKED).count()));
        checkedIn.setText(String.valueOf(todayList.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CHECKED_IN).count()));
        todayCancel.setText(String.valueOf(todayList.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CANCELLED).count()));

        // --- load config into fields ---
        RestaurantConfig cfg = AppData.config;
        totalTableField.setText(String.valueOf(cfg.getMaxTables()));
        maxGuestField.setText(String.valueOf(cfg.getMaxGuest()));
        gapTimeField.setText(String.valueOf(cfg.getGapTimeMinutes()));
        maxAdvanceBooking.setText(String.valueOf(cfg.getMaxAdvanceDays()));
        openTimeField.setText(cfg.getOpenTime().format(TIME_FORMAT));
        closeTimeField.setText(cfg.getCloseTime().format(TIME_FORMAT));

        // --- number only ---
        applyNumberOnly(totalTableField);
        applyNumberOnly(maxGuestField);
        applyNumberOnly(gapTimeField);
        applyNumberOnly(maxAdvanceBooking);

        // --- time format (HH:mm) ---
        applyTimeFormat(openTimeField);
        applyTimeFormat(closeTimeField);

        // --- dirty tracking: save dims until a field in its section changes ---
        setSaveActive(table_save, false);
        setSaveActive(time_save, false);

        totalTableField.textProperty().addListener((o, ov, nv) -> refreshTableSave());
        maxGuestField.textProperty().addListener((o, ov, nv) -> refreshTableSave());

        maxAdvanceBooking.textProperty().addListener((o, ov, nv) -> refreshTimeSave());
        gapTimeField.textProperty().addListener((o, ov, nv) -> refreshTimeSave());
        openTimeField.textProperty().addListener((o, ov, nv) -> refreshTimeSave());
        closeTimeField.textProperty().addListener((o, ov, nv) -> refreshTimeSave());

        // --- คลิกนอก TextField แล้วเลิกโฟกัส ---
        totalTableField.sceneProperty().addListener((obs, oldScene, scene) -> {
            if (scene != null) {
                scene.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
                    if (!(e.getTarget() instanceof TextField)) {
                        scene.getRoot().requestFocus();
                    }
                });
            }
        });

        usernameCol.setCellValueFactory(
                new PropertyValueFactory<>("username")
        );
        roleCol.setCellValueFactory(
                new PropertyValueFactory<>("role")
        );

        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Del");
            private final HBox box = new HBox(6, editBtn, deleteBtn);

            {
                editBtn.getStyleClass().add("btn-icon");
                deleteBtn.getStyleClass().addAll("btn-icon", "btn-icon-danger");
                editBtn.setOnAction(e -> {
                    Staff staff = getTableView().getItems().get(getIndex());
                    handleEdit(staff);
                });
                deleteBtn.setOnAction(e -> {
                    Staff staff = getTableView().getItems().get(getIndex());
                    handleDelete(staff);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        // --- role ComboBox ---
        roleSelect.setItems(FXCollections.observableArrayList(StaffService.Roles.values()));

        // --- โหลด staff จาก StaffService ลงตาราง (ใช้ list เดียวร่วมกัน) ---
        staffItems.setAll(AppData.staffService.getStaffList());
        staffTable.setItems(staffItems);
    }

    private void handleEdit(Staff staff) {
        editingStaff = staff;
        formTitle.setText("Edit Staff");

        // เติมค่าเดิมลงฟอร์ม
        usernameField.setText(staff.getUsername());
        passwordField.setText(staff.getPassword());
        roleSelect.setValue(StaffService.Roles.valueOf(staff.getRole().name()));

        usernameField.setStyle("");
        passwordField.setStyle("");
        roleSelect.setStyle("");

        showPopUp(addStaff);
    }
    private void handleDelete(Staff staff) {
        AppData.staffService.removeStaff(staff);
        staffItems.remove(staff);
    }

    private void showPopUp(VBox button) {
        AnimationUtils.popUpShow(mainContent, popUpPane, blurOverlay, popUpBox, button);
    }
    private void hidePopUp() {
        AnimationUtils.popUpHide(mainContent, popUpPane, blurOverlay, popUpBox);
    }

    private void setSaveActive(VBox box, boolean active) {
        box.setOpacity(active ? 1.0 : 0.4);
        box.setMouseTransparent(!active);
    }

    private void refreshTableSave() { setSaveActive(table_save, tableDirty()); }
    private void refreshTimeSave() { setSaveActive(time_save, timeDirty()); }

    private boolean tableDirty() {
        return !totalTableField.getText().equals(String.valueOf(AppData.config.getMaxTables()))
                || !maxGuestField.getText().equals(String.valueOf(AppData.config.getMaxGuest()));
    }

    private boolean timeDirty() {
        return !maxAdvanceBooking.getText().equals(String.valueOf(AppData.config.getMaxAdvanceDays()))
                || !gapTimeField.getText().equals(String.valueOf(AppData.config.getGapTimeMinutes()))
                || !openTimeField.getText().equals(AppData.config.getOpenTime().format(TIME_FORMAT))
                || !closeTimeField.getText().equals(AppData.config.getCloseTime().format(TIME_FORMAT));
    }

    private int parseIntOr(TextField field, int fallback) {
        try {
            return Integer.parseInt(field.getText().trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    @FXML
    private void saveTable() {
        if (!tableDirty()) return;

        AppData.config.setMaxTables(parseIntOr(totalTableField, AppData.config.getMaxTables()));
        AppData.config.setMaxGuest(parseIntOr(maxGuestField, AppData.config.getMaxGuest()));

        // normalize displayed values back from config
        totalTableField.setText(String.valueOf(AppData.config.getMaxTables()));
        maxGuestField.setText(String.valueOf(AppData.config.getMaxGuest()));

        setSaveActive(table_save, false);
    }

    @FXML
    private void saveTime() {
        if (!timeDirty()) return;

        LocalTime open, close;
        try {
            open = LocalTime.parse(openTimeField.getText(), TIME_FORMAT);
            close = LocalTime.parse(closeTimeField.getText(), TIME_FORMAT);
        } catch (DateTimeParseException e) {
            // เวลาไม่ถูก format — ไม่บันทึก ปล่อยให้ปุ่มยังเข้มไว้
            return;
        }

        AppData.config.setOpenTime(open);
        AppData.config.setCloseTime(close);
        AppData.config.setGapTimeMinutes(parseIntOr(gapTimeField, AppData.config.getGapTimeMinutes()));
        AppData.config.setMaxAdvanceDays(parseIntOr(maxAdvanceBooking, AppData.config.getMaxAdvanceDays()));

        // คำนวณ time slot ใหม่ตาม config ที่เปลี่ยน
        AppData.bookingService.recalculate();

        // normalize displayed values back from config
        gapTimeField.setText(String.valueOf(AppData.config.getGapTimeMinutes()));
        maxAdvanceBooking.setText(String.valueOf(AppData.config.getMaxAdvanceDays()));
        openTimeField.setText(open.format(TIME_FORMAT));
        closeTimeField.setText(close.format(TIME_FORMAT));

        setSaveActive(time_save, false);
    }

    private void applyNumberOnly(TextField field) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) field.setText(newVal.replaceAll("\\D", ""));
        });
    }

    private void applyTimeFormat(TextField field) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            // อนุญาตเฉพาะตัวเลขและ ':'
            String filtered = newVal.replaceAll("[^0-9:]", "");
            if (!filtered.equals(newVal)) {
                field.setText(filtered);
                return;
            }
            // auto-insert ':' หลังพิมพ์ 2 ตัว
            if (newVal.length() == 2 && oldVal.length() == 1 && !newVal.contains(":")) {
                field.setText(newVal + ":");
            }
            // จำกัดความยาว HH:mm = 5
            if (newVal.length() > 5) field.setText(oldVal);
        });

        field.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) {
                try {
                    LocalTime parsed = LocalTime.parse(field.getText(), TIME_FORMAT);
                    field.setText(parsed.format(TIME_FORMAT));
                    field.setStyle("");
                } catch (DateTimeParseException e) {
                    field.setStyle("-fx-border-color: red;");
                }
            }
        });
    }

    @FXML
    private void goToStaff() {
        SceneManager.switchScene("staffDashBoard.fxml", SceneManager.TransitionType.SLIDE_OUT);
    }
}
