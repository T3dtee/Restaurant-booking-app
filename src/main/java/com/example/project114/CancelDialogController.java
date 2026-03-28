package com.example.project114;

import javafx.fxml.FXML;
import javafx.stage.Stage;

public class CancelDialogController {
    private Stage dialogStage;
    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    private boolean confirmed;
    public boolean isConfirmed(){
        return confirmed;
    }

    @FXML
    private void handleConfirm() {
        dialogStage.close();
    }

    @FXML
    private void confirmOnClick(){
        confirmed = true;
        handleConfirm();
    }
    @FXML
    private void closeOnClick(){
        confirmed = false;
        handleConfirm();
    }
}
