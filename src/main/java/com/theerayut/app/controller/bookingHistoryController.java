package com.theerayut.app.controller;

import com.theerayut.app.AppData;
import com.theerayut.app.model.Reservation;
import com.theerayut.app.util.AnimationUtils;
import com.theerayut.app.util.SceneManager;
import javafx.animation.*;
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

        for (Reservation data : customerReservations) {
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

            if (data.getReservationId().equals(AppData.bookingId)) {
                controller.setOnAdd(() -> {
                    Region regionItem = (Region) item;

                    double height = regionItem.prefHeightProperty().get();
                    regionItem.setTranslateX(-440);
                    regionItem.setPrefHeight(0);

                    Timeline collapse = new Timeline(
                            new KeyFrame(Duration.millis(250),
                                    new KeyValue(regionItem.prefHeightProperty(), height, Interpolator.SPLINE(0.3, 0.2, 0.6, 0.95))
                            )
                    );

                    TranslateTransition trans = new TranslateTransition(Duration.millis(300), regionItem);
                    trans.setToX(0);
                    trans.setInterpolator(Interpolator.SPLINE(0.4, 1, 0.6, 1));

                    ScaleTransition s1 = new ScaleTransition(Duration.millis(120), regionItem);
                    s1.setToX(0.94); s1.setToY(1.04);
                    s1.setInterpolator(Interpolator.SPLINE(0.4, 0, 0.6, 1));

                    ScaleTransition s2 = new ScaleTransition(Duration.millis(100), regionItem);
                    s2.setToX(1.0); s2.setToY(1.0);
                    s2.setInterpolator(Interpolator.SPLINE(0.4, 0, 0.6, 1));

                    s1.setOnFinished(e -> s2.play());

                    Timeline timeline = new Timeline(
                            new KeyFrame(Duration.millis(100), event -> collapse.play()),
                            new KeyFrame(Duration.millis(150), event -> trans.play()),
                            new KeyFrame(Duration.millis(350), event -> s1.play())
                    );

                    timeline.play();
                });
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
