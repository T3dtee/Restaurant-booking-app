package com.theerayut.app.controller;

import com.theerayut.app.AppData;
import com.theerayut.app.model.Reservation;
import com.theerayut.app.model.ReservationStatus;
import com.theerayut.app.util.AnimationUtils;
import com.theerayut.app.util.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.CacheHint;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class bookingHistoryController {
    @FXML private VBox itemBox;
    @FXML private AnchorPane mainContent;
    @FXML private AnchorPane popUp;
    @FXML private VBox popUpBox;
    @FXML private Pane blurOverlay;
    @FXML private VBox backBtnBox;
    @FXML private Label noBookingText;

    @FXML
    private void goToBooking() {SceneManager.switchScene("booking.fxml", SceneManager.TransitionType.SLIDE_OUT);}

    public void initialize() {
        AnimationUtils.buttonHover(backBtnBox, 11, 100);
        try { loadItems(); } catch (IOException e) { e.printStackTrace(); }
        if (cardMoveInAction != null) SceneManager.setAfterTransitionCallback(cardMoveInAction);
    }

    private Runnable pendingCancelAction;
    private Runnable cardMoveInAction;

    private void loadItems() throws IOException {
        List<Reservation> customerReservations = AppData.allBookingData.getReservationsByCustomer(AppData.loginUserData).stream()
                .sorted(Comparator.comparing((Reservation r) -> {
                            if (r.getStatus() == ReservationStatus.BOOKED) return -1;
                            else if (r.getStatus() == ReservationStatus.CHECKED_IN) return 0;
                            else if (r.getStatus() == ReservationStatus.CANCELLED) return 1;
                            else return 2;})
                        .thenComparing(Comparator.comparing(Reservation::getDate).reversed())
                        .thenComparing(Comparator.comparing(Reservation::getTime).reversed())
                        .thenComparing(Reservation::getTableNo)
                ).toList();

        ReservationStatus lastStatus = null;
        bookingHeadStatusCard statusController = null;
        int groupIndex = 0;

        if (!customerReservations.isEmpty()) {
            noBookingText.setVisible(false);
        }

        Set<ReservationStatus> expandedStatuses = resolveExpandedStatuses(customerReservations);

        for (Reservation data : customerReservations) {
            if (lastStatus == null || lastStatus != data.getStatus()) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/example/app/ui/headStatus.fxml")
                );
                Parent head = loader.load();
                head.setCache(true);
                head.setCacheHint(CacheHint.SPEED);

                statusController = loader.getController();
                statusController.setData(data.getStatus(), customerReservations);
                if (!expandedStatuses.contains(data.getStatus())) {
                    statusController.collapseInitially();
                }
                lastStatus = data.getStatus();
                groupIndex = 0;
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

            Region regionItem = (Region) item;
            final double naturalHeight = regionItem.getPrefHeight();

            final int cardIndex = groupIndex;

            controller.setOnHide(() -> AnimationUtils.cardHide(regionItem));
            controller.setOnAdd(() -> AnimationUtils.cardMoveInAt(regionItem, naturalHeight, cardIndex));

            if (statusController != null) {
                statusController.addHideCard(controller::handleHide);
                statusController.addShowCard(controller::showCard);
            }

            if (!expandedStatuses.contains(data.getStatus())) {
                regionItem.setPrefHeight(0);
                regionItem.setOpacity(0);
            }

            if (data.getReservationId().equals(AppData.bookingId)) {
                cardMoveInAction = controller::moveInAction;
            }

            itemBox.getChildren().add(item);
            groupIndex++;
        }
    }

    // Decide which status groups start expanded. BOOKED and CHECKED_IN open by
    // default; if neither is present, fall back down the priority chain
    // (CANCELLED, then EXPIRED) so at least one group is always open.
    private Set<ReservationStatus> resolveExpandedStatuses(List<Reservation> reservations) {
        Set<ReservationStatus> present = EnumSet.noneOf(ReservationStatus.class);
        for (Reservation r : reservations) present.add(r.getStatus());

        Set<ReservationStatus> expanded = EnumSet.noneOf(ReservationStatus.class);
        if (present.contains(ReservationStatus.BOOKED)) expanded.add(ReservationStatus.BOOKED);
        if (present.contains(ReservationStatus.CHECKED_IN)) expanded.add(ReservationStatus.CHECKED_IN);

        if (expanded.isEmpty()) {
            if (present.contains(ReservationStatus.CANCELLED)) expanded.add(ReservationStatus.CANCELLED);
            else if (present.contains(ReservationStatus.EXPIRED)) expanded.add(ReservationStatus.EXPIRED);
        }
        return expanded;
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
