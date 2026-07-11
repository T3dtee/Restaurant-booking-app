package com.theerayut.app.controller;

import com.theerayut.app.AppData;
import com.theerayut.app.model.Person;
import com.theerayut.app.model.Reservation;
import com.theerayut.app.model.ReservationStatus;
import com.theerayut.app.model.RestaurantConfig;
import com.theerayut.app.model.Staff;
import com.theerayut.app.service.StaffService;
import com.theerayut.app.util.AnimationUtils;
import com.theerayut.app.util.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

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
    @FXML private VBox fieldPopUpBox;
    @FXML private TextField usernameField;
    @FXML private TextField passwordField;
    @FXML private ComboBox<StaffService.Roles> roleSelect;
    @FXML private VBox addStaff;
    @FXML private Label formTitle;
    @FXML private VBox popUpBox;
    @FXML private VBox backBtn;
    @FXML private Label deleteTitle;
    @FXML private Label deleteText;
    @FXML private Button deleteConfirmBtn;
    @FXML private Button closePopUpBtn;

    private final ObservableList<Staff> staffItems = FXCollections.observableArrayList();
    private Staff editingStaff;
    private Staff pendingDeleteStaff;

    @FXML
    public void initialize() {
        AnimationUtils.buttonHover(backBtn, 11, 100);
        initStats();
        initConfigFields();
        initStaffTable();
    }

    private void initStats() {
        List<Reservation> today = AppData.allBookingData.getReservationsByDate(LocalDate.now());
        todayBook.setText(String.valueOf(today.stream()
                .filter(r -> r.getStatus() == ReservationStatus.BOOKED).count()));
        checkedIn.setText(String.valueOf(today.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CHECKED_IN
                          || r.getStatus() == ReservationStatus.EXPIRED).count()));
        todayCancel.setText(String.valueOf(today.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CANCELLED).count()));
    }

    private void initConfigFields() {
        RestaurantConfig cfg = AppData.config;
        totalTableField.setText(String.valueOf(cfg.getMaxTables()));
        maxGuestField.setText(String.valueOf(cfg.getMaxGuest()));
        gapTimeField.setText(String.valueOf(cfg.getGapTimeMinutes()));
        maxAdvanceBooking.setText(String.valueOf(cfg.getMaxAdvanceDays()));
        openTimeField.setText(cfg.getOpenTime().format(TIME_FORMAT));
        closeTimeField.setText(cfg.getCloseTime().format(TIME_FORMAT));

        applyNumberOnly(totalTableField);
        applyNumberOnly(maxGuestField);
        applyNumberOnly(gapTimeField);
        applyNumberOnly(maxAdvanceBooking);
        applyTimeFormat(openTimeField);
        applyTimeFormat(closeTimeField);

        setSaveActive(table_save, false);
        setSaveActive(time_save, false);

        totalTableField.textProperty().addListener((o, ov, nv) -> refreshTableSave());
        maxGuestField.textProperty().addListener((o, ov, nv) -> refreshTableSave());
        maxAdvanceBooking.textProperty().addListener((o, ov, nv) -> refreshTimeSave());
        gapTimeField.textProperty().addListener((o, ov, nv) -> refreshTimeSave());
        openTimeField.textProperty().addListener((o, ov, nv) -> refreshTimeSave());
        closeTimeField.textProperty().addListener((o, ov, nv) -> refreshTimeSave());

        totalTableField.sceneProperty().addListener((obs, oldScene, scene) -> {
            if (scene != null) {
                scene.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
                    if (!(e.getTarget() instanceof TextField)) scene.getRoot().requestFocus();
                });
            }
        });
    }

    @FXML
    private void saveTable() {
        if (!tableDirty()) return;

        int tables = parseIntOr(totalTableField, 0);
        int guest  = parseIntOr(maxGuestField, 0);

        boolean valid = true;
        if (tables <= 0) { markInvalid(totalTableField);
            valid = false; }
        else totalTableField.setStyle("");
        if (guest  <= 0) { markInvalid(maxGuestField);
            valid = false; }
        else maxGuestField.setStyle("");
        if (!valid) return;

        AppData.config.setMaxTables(tables);
        AppData.config.setMaxGuest(guest);
        AppData.config.save();

        totalTableField.setText(String.valueOf(AppData.config.getMaxTables()));
        maxGuestField.setText(String.valueOf(AppData.config.getMaxGuest()));
        setSaveActive(table_save, false);
    }

    @FXML
    private void saveTime() {
        if (!timeDirty()) return;

        LocalTime open, close;
        try {
            open  = LocalTime.parse(openTimeField.getText(),  TIME_FORMAT);
            close = LocalTime.parse(closeTimeField.getText(), TIME_FORMAT);
        } catch (DateTimeParseException e) {
            return;
        }

        int gap = parseIntOr(gapTimeField, 0);

        boolean valid = true;
        if (gap <= 0) { markInvalid(gapTimeField);
            valid = false; }
        else gapTimeField.setStyle("");
        if (!close.isAfter(open)) { markInvalid(closeTimeField);
            valid = false; }
        else closeTimeField.setStyle("");
        if (!valid) return;

        AppData.config.setOpenTime(open);
        AppData.config.setCloseTime(close);
        AppData.config.setGapTimeMinutes(gap);
        AppData.config.setMaxAdvanceDays(parseIntOr(maxAdvanceBooking, AppData.config.getMaxAdvanceDays()));
        AppData.config.save();
        AppData.bookingService.recalculate();

        gapTimeField.setText(String.valueOf(AppData.config.getGapTimeMinutes()));
        maxAdvanceBooking.setText(String.valueOf(AppData.config.getMaxAdvanceDays()));
        openTimeField.setText(open.format(TIME_FORMAT));
        closeTimeField.setText(close.format(TIME_FORMAT));
        setSaveActive(time_save, false);
    }

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

    private void setSaveActive(VBox box, boolean active) {
        box.setOpacity(active ? 1.0 : 0.4);
        box.setMouseTransparent(!active);
    }

    private void refreshTableSave() { setSaveActive(table_save, tableDirty()); }
    private void refreshTimeSave()  { setSaveActive(time_save,  timeDirty()); }

    private void applyNumberOnly(TextField field) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) field.setText(newVal.replaceAll("\\D", ""));
            else field.setStyle("");
        });
    }

    private void applyTimeFormat(TextField field) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            field.setStyle("");
            String filtered = newVal.replaceAll("[^0-9:]", "");
            if (!filtered.equals(newVal)) { field.setText(filtered); return; }
            if (newVal.length() == 2 && oldVal.length() == 1 && !newVal.contains(":")) field.setText(newVal + ":");
            if (newVal.length() > 5) field.setText(oldVal);
        });
        field.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) {
                try {
                    field.setText(LocalTime.parse(field.getText(), TIME_FORMAT).format(TIME_FORMAT));
                    field.setStyle("");
                } catch (DateTimeParseException e) {
                    field.setStyle("-fx-border-color: red;");
                }
            }
        });
    }

    private void initStaffTable() {
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn   = new Button("Edit");
            private final Button deleteBtn = new Button("Del");
            private final HBox   box       = new HBox(6, editBtn, deleteBtn);

            {
                editBtn.getStyleClass().add("btn-icon");
                deleteBtn.getStyleClass().addAll("btn-icon", "btn-icon-danger");
                editBtn.setOnAction(e   -> handleEdit(getTableView().getItems().get(getIndex())));
                deleteBtn.setOnAction(e -> handleDelete(getTableView().getItems().get(getIndex()), deleteBtn));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        roleSelect.setItems(FXCollections.observableArrayList(StaffService.Roles.values()));
        staffItems.setAll(AppData.staffService.getStaffList());
        staffTable.setItems(staffItems);
    }

    @FXML
    private void addStaff() {
        resetForm();
        formTitle.setText("Add Staff");
        showPopUp(addStaff);
    }

    private void handleEdit(Staff staff) {
        editingStaff = staff;
        formTitle.setText("Edit Staff");
        usernameField.setText(staff.getUsername());
        passwordField.setText(staff.getPassword());
        roleSelect.setValue(StaffService.Roles.valueOf(staff.getRole().name()));
        usernameField.setStyle("");
        passwordField.setStyle("");
        roleSelect.setStyle("");
        showPopUp(addStaff);
    }

    private void handleDelete(Staff staff, Button sourceBtn) {
        boolean isSelf = staff.getUsername().equals(AppData.loginStaffData.getUsername());
        closePopUpBtn.setTranslateX(0);
        if (isSelf) {
            deleteTitle.setText("Cannot delete your own account");
            deleteText.setText("You cannot remove the account you are currently logged in with.");
            closePopUpBtn.setTranslateX(80);
            deleteConfirmBtn.setVisible(false);
        } else {
            deleteTitle.setText("Delete staff account?");
            deleteText.setText("Are you sure you want to delete this staff user?");
            deleteConfirmBtn.setVisible(true);
            pendingDeleteStaff = staff;
        }
        AnimationUtils.popUpShow(mainContent, popUpPane, blurOverlay, popUpBox, sourceBtn);
    }

    @FXML
    private void formSaveOnClick() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        StaffService.Roles role = roleSelect.getValue();

        boolean valid = true;
        if (username.isEmpty()) { markInvalid(usernameField); valid = false; }
        if (password.isEmpty()) { markInvalid(passwordField); valid = false; }
        if (role == null)       { roleSelect.setStyle("-fx-border-color: red;"); valid = false; }
        if (!valid) return;

        boolean duplicate = AppData.staffService.getStaffList().stream()
                .anyMatch(s -> s != editingStaff && s.getUsername().equalsIgnoreCase(username));
        if (duplicate) { markInvalid(usernameField); return; }

        Staff staff = new Staff(username, password, Person.Roles.valueOf(role.name()));

        if (editingStaff != null) {
            boolean editingSelf = editingStaff.getUsername().equals(AppData.loginStaffData.getUsername());
            int idx = staffItems.indexOf(editingStaff);
            AppData.staffService.removeStaff(editingStaff);
            AppData.staffService.addStaff(staff);
            if (idx >= 0) staffItems.set(idx, staff);
            if (editingSelf) AppData.loginStaffData = staff;
        } else {
            AppData.staffService.addStaff(staff);
            staffItems.add(staff);
        }

        resetForm();
        hidePopUp();
    }

    @FXML
    private void formCancelOnClick() {
        resetForm();
        hidePopUp();
    }

    @FXML
    private void popUpCloseOnClick() {
        pendingDeleteStaff = null;
        AnimationUtils.popUpHide(mainContent, popUpPane, blurOverlay, popUpBox);
    }

    @FXML
    private void popUpConfirmOnClick() {
        if (pendingDeleteStaff != null) {
            AppData.staffService.removeStaff(pendingDeleteStaff);
            staffItems.remove(pendingDeleteStaff);
            pendingDeleteStaff = null;
        }
        AnimationUtils.popUpHide(mainContent, popUpPane, blurOverlay, popUpBox);
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

    private void markInvalid(TextField field) { field.setStyle("-fx-border-color: red;"); }

    private int parseIntOr(TextField field, int fallback) {
        try { return Integer.parseInt(field.getText().trim()); }
        catch (NumberFormatException e) { return fallback; }
    }

    private void showPopUp(VBox content) {
        AnimationUtils.popUpShow(mainContent, popUpPane, blurOverlay, fieldPopUpBox, content);
    }

    private void hidePopUp() {
        AnimationUtils.popUpHide(mainContent, popUpPane, blurOverlay, fieldPopUpBox);
    }

    @FXML
    private void goToStaff() {
        SceneManager.switchScene("staffDashBoard.fxml", SceneManager.TransitionType.SLIDE_OUT);
    }
}
