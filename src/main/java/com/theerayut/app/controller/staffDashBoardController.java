package com.theerayut.app.controller;

import com.theerayut.app.AppData;
import com.theerayut.app.model.Reservation;
import com.theerayut.app.model.ReservationStatus;
import com.theerayut.app.util.AnimationUtils;
import com.theerayut.app.util.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class staffDashBoardController {

    @FXML
    private void goToStaffLogin() { SceneManager.switchScene("login-staff.fxml");}

    @FXML
    private VBox itemBox;
    @FXML
    private Label noOrderText;
    @FXML
    private AnchorPane mainContent;
    @FXML
    private AnchorPane popUp;
    @FXML
    private VBox popUpBox;
    @FXML
    private Pane blurOverlay;

    List<Reservation> sortedReservations;
    private Runnable pendingCancelAction;

    private void loadItems() throws IOException {
         sortedReservations = AppData.allBookingData.getAllReservations().stream()
                .filter(r -> r.getStatus() == ReservationStatus.BOOKED) // กรองเอาเฉพาะคิวที่ถูกจอง
                .sorted(Comparator.comparing(Reservation::getDate)
                        .thenComparing(Reservation::getTime)
                        .thenComparing(Reservation::getTableNo)) // เรียงวันที่ ตามด้วยเวลา แล้วเลขโต๊ะ
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

    public void initialize() {
        try {
            loadItems();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!sortedReservations.isEmpty()) {
            noOrderText.setVisible(false);
        }
    }

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

    private void update(){
       if (AppData.allBookingData.getAllReservations().stream().noneMatch(r -> r.getStatus() == ReservationStatus.BOOKED)){
           noOrderText.setVisible(true);
       }
    }
}
