package com.theerayut.app.controller;

import com.theerayut.app.AppData;
import com.theerayut.app.model.Reservation;
import com.theerayut.app.model.ReservationStatus;
import com.theerayut.app.util.AnimationUtils;
import com.theerayut.app.util.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.CacheHint;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

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
    private VBox backBtnBox;
    @FXML
    private ImageView icon;

    @FXML
    private void goToBooking() {SceneManager.switchScene("booking.fxml", SceneManager.TransitionType.SLIDE_OUT);}

    public void initialize() {
        AnimationUtils.backToHomeBtn(backBtnBox, icon);
        try {
            loadItems();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (cardMoveInAction != null) Platform.runLater(cardMoveInAction);
    }

    private Runnable pendingCancelAction;
    private Runnable cardMoveInAction;

    private void loadItems() throws IOException {
        List<Reservation> customerReservations = AppData.allBookingData.getReservationsByCustomer(AppData.loginUserData);
        ReservationStatus lastStatus = null;

        boolean multipleStatuses = customerReservations.stream()
                .map(Reservation::getStatus)
                .distinct()
                .limit(2)  // ไม่ต้องนับทั้งหมด แค่เจอ 2 อันก็หยุด
                .count() > 1;

        bookingHeadStatusCard statusController = null;
        for (Reservation data : customerReservations) {

            if (multipleStatuses && (lastStatus == null || lastStatus != data.getStatus())) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/example/app/ui/headStatus.fxml")
                );
                Parent head =  loader.load();
                head.setCache(true);
                head.setCacheHint(CacheHint.SPEED);

                statusController = loader.getController();
                statusController.setData(data.getStatus());
                lastStatus = data.getStatus();

                itemBox.getChildren().add(head);
            }

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/app/ui/bookingHistoryContainer.fxml"));

            Parent item = loader.load();

            item.setCache(true);
            item.setCacheHint(CacheHint.SPEED);

            bookingHistoryCardController controller = loader.getController();
            controller.setData(data);
            controller.setOnCancelRequest(() -> {
                pendingCancelAction = controller::confirmCancel;
                showPopUp(controller.getCancelBtn());
            });

            controller.setOnRemove(() ->
                AnimationUtils.cardRemove((Region) item, () -> itemBox.getChildren().remove(item))
            );

            Region regionItem = (Region) item;
            final double naturalHeight = regionItem.getPrefHeight();

            controller.setOnHide(() -> AnimationUtils.cardHide(regionItem));
            controller.setOnAdd(() -> AnimationUtils.cardMoveIn(regionItem, naturalHeight));

            if (statusController != null) {
                statusController.addHideCard(controller::handleHide);
                statusController.addShowCard(controller::showCard);
            }

            if (data.getReservationId().equals(AppData.bookingId)) {
                cardMoveInAction = controller::moveInAction;
            }

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

    private void showPopUp(Button button) {
        AnimationUtils.popUpShow(mainContent, popUp, blurOverlay, popUpBox, button);
    }

    private void hidePopUp() {
        AnimationUtils.popUpHide(mainContent, popUp, blurOverlay, popUpBox);
    }
}
