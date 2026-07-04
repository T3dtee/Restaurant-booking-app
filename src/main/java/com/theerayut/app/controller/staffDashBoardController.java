package com.theerayut.app.controller;

import com.theerayut.app.AppData;
import com.theerayut.app.model.Person;
import com.theerayut.app.model.Reservation;
import com.theerayut.app.model.ReservationStatus;
import com.theerayut.app.util.AnimationUtils;
import com.theerayut.app.util.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class staffDashBoardController {

    @FXML private VBox itemBox;
    @FXML private Label noOrderText;
    @FXML private AnchorPane mainContent;
    @FXML private AnchorPane popUp;
    @FXML private VBox popUpBox;
    @FXML private Pane blurOverlay;
    @FXML private VBox menuBtn;
    @FXML private VBox sideBar;
    @FXML private Button sbAdminBtn;
    @FXML private ImageView sbCloseIcon;
    @FXML private ImageView sbDashIcon;
    @FXML private ImageView sbAdminIcon;
    @FXML private ImageView sbLogoutIcon;

    List<Reservation> sortedReservations;
    private Runnable pendingCancelAction;

    public void initialize() {
        AnimationUtils.buttonHover(menuBtn, 11, 100);

        boolean isAdmin = AppData.loginStaffData.getRole() == Person.Roles.Admin;
        if (!isAdmin) sbAdminBtn.setDisable(true);

        Color gray = Color.web("#424242");
        sbCloseIcon.setImage(AnimationUtils.recolor(sbCloseIcon.getImage(), gray));
        sbDashIcon.setImage(AnimationUtils.recolor(sbDashIcon.getImage(), gray));
        sbAdminIcon.setImage(AnimationUtils.recolor(sbAdminIcon.getImage(), gray));
        sbLogoutIcon.setImage(AnimationUtils.recolor(sbLogoutIcon.getImage(), gray));

        try {
            loadItems();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!sortedReservations.isEmpty()) {
            noOrderText.setVisible(false);
        }
    }

    private void loadItems() throws IOException {
        sortedReservations = AppData.allBookingData.getAllReservations().stream()
                .filter(r -> r.getStatus() == ReservationStatus.BOOKED)
                .sorted(Comparator.comparing(Reservation::getDate)
                        .thenComparing(Reservation::getTime)
                        .thenComparing(Reservation::getTableNo))
                .collect(Collectors.toList());

        for (Reservation data : sortedReservations) {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/app/ui/bookingContainer.fxml"));

            Parent item = loader.load();

            staffDBCardController controller = loader.getController();
            controller.setData(data);
            controller.setOnCancelRequest(() -> {
                pendingCancelAction = controller::confirmCancel;
                showPopUp(controller.getCancelBtn());
            });

            controller.setOnRemove(() ->
                AnimationUtils.cardRemove((Region) item, () -> {
                    itemBox.getChildren().remove(item);
                    update();
                })
            );

            itemBox.getChildren().add(item);
        }
    }

    @FXML private void menuOnClick() { AnimationUtils.sideBarShow(mainContent, popUp, blurOverlay, sideBar); }
    @FXML private void hideSideBarOnClick() { AnimationUtils.sideBarHide(mainContent, popUp, blurOverlay, sideBar); }
    @FXML private void dashboardOnClick() { AnimationUtils.sideBarHide(mainContent, popUp, blurOverlay, sideBar); }
    @FXML private void adminSideOnClick() { SceneManager.switchScene("admin.fxml", SceneManager.TransitionType.SLIDE_IN); }
    @FXML private void logoutOnClick() { SceneManager.switchScene("login-staff.fxml"); }

    @FXML
    private void closeOnClick() {
        hidePopUp();
        pendingCancelAction = null;
    }

    @FXML
    private void confirmOnClick() {
        hidePopUp();
        if (pendingCancelAction != null) {
            pendingCancelAction.run();
            pendingCancelAction = null;
        }
    }

    private void showPopUp(Button button) {
        AnimationUtils.popUpShow(mainContent, popUp, blurOverlay, popUpBox, button);
    }

    private void hidePopUp() {
        AnimationUtils.popUpHide(mainContent, popUp, blurOverlay, popUpBox);
    }

    private void update() {
        if (AppData.allBookingData.getAllReservations().stream().noneMatch(r -> r.getStatus() == ReservationStatus.BOOKED)) {
            noOrderText.setVisible(true);
        }
    }
}
