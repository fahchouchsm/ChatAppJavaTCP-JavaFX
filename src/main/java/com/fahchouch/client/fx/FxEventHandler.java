package com.fahchouch.client.fx;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class FxEventHandler {
    public static void showAlert(String msg) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
