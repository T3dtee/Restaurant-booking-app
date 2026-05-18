package com.example.project114;

import com.example.project114.backend.Reservation;
import javafx.animation.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class bookingHistotyController {
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

    private List<Reservation> customerReservations;
    private Runnable pendingCancelAction;

    private void loadItems() throws IOException {
        customerReservations = AppData.allBookingData.getReservationsByCustomer(AppData.loginUserData);

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

                ft.setOnFinished(a -> {
                    pt.play();
                });
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
        GaussianBlur blur = new GaussianBlur(0);
        mainContent.setEffect(blur);
        popUpBox.setOpacity(0);
        ScaleTransition s1 = new ScaleTransition(Duration.millis(80), popUpBox);
        s1.setFromX(0.9);
        s1.setFromY(0.9);
        s1.setToX(1);
        s1.setToY(1);
        s1.setInterpolator(Interpolator.EASE_OUT);

        FadeTransition f = new FadeTransition(Duration.millis(80), popUpBox);
        f.setFromValue(0);
        f.setToValue(1);
        f.setInterpolator(Interpolator.EASE_OUT);

        Timeline t = new Timeline(
                new KeyFrame(Duration.seconds(0.2),
                        new KeyValue(blur.radiusProperty(), 10)
                )
        );

        ObjectProperty<Color> color = new SimpleObjectProperty<>(
                Color.rgb(0, 0, 0, 0.0)
        );

        color.addListener((obs, oldV, newV) -> {
            blurOverlay.setStyle(String.format(
                    "-fx-background-color: rgba(%d,%d,%d,%.3f);",
                    (int)(newV.getRed() * 255),
                    (int)(newV.getGreen() * 255),
                    (int)(newV.getBlue() * 255),
                    newV.getOpacity()
            ));
        });

        Timeline t1 = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(color, Color.rgb(0,0,0,0.0))
                ),
                new KeyFrame(Duration.seconds(0.2),
                        new KeyValue(color, Color.rgb(0,0,0,0.15))
                )
        );
        Timeline t2 = new Timeline(
                new KeyFrame(Duration.millis(80),event -> {
                    s1.play();
                    f.play();})
        );

        popUp.setVisible(true);
        t.play();
        t1.play();
        t2.play();
    }

    private void hidePopUp() {
        GaussianBlur blur = new GaussianBlur(10);
        mainContent.setEffect(blur);

        ScaleTransition s1 = new ScaleTransition(Duration.millis(120), popUpBox);
        s1.setFromX(1);
        s1.setFromY(1);
        s1.setToX(0.8);
        s1.setToY(0.8);

        FadeTransition f = new FadeTransition(Duration.millis(120), popUpBox);
        f.setFromValue(1);
        f.setToValue(0);
        f.setOnFinished(e -> {
            popUp.setVisible(false);
            mainContent.setEffect(null);});

        Timeline t = new Timeline(
                new KeyFrame(Duration.seconds(0.12),
                        new KeyValue(blur.radiusProperty(), 0)
                )
        );

        ObjectProperty<Color> color = new SimpleObjectProperty<>(
                Color.rgb(0, 0, 0, 0.0)
        );
        color.addListener((obs, oldV, newV) -> {
            blurOverlay.setStyle(String.format(
                    "-fx-background-color: rgba(%d,%d,%d,%.3f);",
                    (int)(newV.getRed() * 255),
                    (int)(newV.getGreen() * 255),
                    (int)(newV.getBlue() * 255),
                    newV.getOpacity()
            ));
        });

        Timeline t1 = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(color, Color.rgb(0,0,0,0.15))
                ),
                new KeyFrame(Duration.seconds(0.12),
                        new KeyValue(color, Color.rgb(0,0,0,0.0))
                )
        );

        f.play();
        s1.play();
        t.play();
        t1.play();
    }
}
