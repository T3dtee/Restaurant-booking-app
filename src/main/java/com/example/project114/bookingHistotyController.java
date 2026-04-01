package com.example.project114;

import com.example.project114.backend.Reservation;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class bookingHistotyController {
    @FXML
    private VBox itemBox;

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

    private void loadItems() throws IOException {
        customerReservations = AppData.allBookingData.getReservationsByCustomer(AppData.loginUserData);

        for (Reservation data : customerReservations) {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/project114/ui/bookingHistoryContainer.fxml"));

            Parent item = loader.load();

            bookingHistoryCardController controller = loader.getController();
            controller.setData(data);
            controller.setOnCancelRequest(this::cancelDialog);

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

    public boolean cancelDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/project114/ui/CancelDialog.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL); // ล็อคหน้าจอหลักไว้จนกว่าจะปิดอันนี้
            stage.initStyle(StageStyle.UNDECORATED); // เอาแถบหัวหน้าต่างแบบ Windows/Mac ออกเพื่อให้ Minimal สุดๆ
            stage.setScene(new Scene(root));

            CancelDialogController controller = loader.getController();
            controller.setDialogStage(stage);

            stage.showAndWait(); // รอจนกว่าจะปิดหน้าต่าง

            return controller.isConfirmed(); // ส่งค่ากลับว่าตกลงหรือยกเลิก
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
