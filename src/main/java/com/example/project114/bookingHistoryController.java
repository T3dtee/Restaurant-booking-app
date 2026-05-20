package com.example.project114;

import com.example.project114.backend.Reservation;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class bookingHistoryController {
    @FXML
    private VBox itemBox;
    @FXML
    private AnchorPane mainContent;
    @FXML
    private AnchorPane popUp;
    @FXML
    private VBox popUpBox;
    @FXML
    private Pane blurOverlay;


    @FXML
    private void goToBooking() {SceneManager.switchScene("booking.fxml", "booking.css");}

    public void initialize() {
        try {
            loadItems();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Runnable pendingCancelAction;

    private void loadItems() throws IOException {
        List<Reservation> customerReservations = AppData.allBookingData.getReservationsByCustomer(AppData.loginUserData);

        for (Reservation data : customerReservations) {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/project114/ui/bookingHistoryContainer.fxml"));

            Parent item = loader.load();

            bookingHistoryCardController controller = loader.getController();
            controller.setData(data);
            controller.setOnCancelRequest(() -> {
                pendingCancelAction = controller::confirmCancel;
                showPopUp();
            });

            controller.setOnRemove(() -> {
                Region regionItem = (Region) item;

                // 1. Fade ออก
                FadeTransition ft = new FadeTransition(Duration.millis(250), regionItem);
                ft.setToValue(0);

                regionItem.setMinHeight(regionItem.getHeight());

                Timeline collapse = new Timeline(
                        new KeyFrame(Duration.millis(330),
                                new KeyValue(regionItem.prefHeightProperty(), 0, Interpolator.EASE_BOTH),
                                new KeyValue(regionItem.minHeightProperty(), 0, Interpolator.EASE_BOTH)
                        )
                );

                ParallelTransition pt = new ParallelTransition(collapse);
                pt.setOnFinished(e -> {
                    itemBox.getChildren().remove(regionItem);
                });

                ft.setOnFinished(e -> pt.play());
                ft.play();
            });

            itemBox.getChildren().add(item);
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

    private void showPopUp() {
        AnimationUtils.popUpShow(mainContent, popUp, blurOverlay, popUpBox);
    }

    private void hidePopUp() {
        AnimationUtils.popUpHide(mainContent, popUp, blurOverlay, popUpBox);
    }
}
