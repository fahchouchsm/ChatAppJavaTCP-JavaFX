package com.fahchouch.client.controllers.login;

import com.fahchouch.client.Client;
import com.fahchouch.client.fx.FxEventHandler;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public class LoginPageController {

    @FXML
    private TextField usernameField;

    @FXML
    private Button connectButton;

    @FXML
    public void initialize() {
        usernameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleConnect();
            }
        });
    }

    @FXML
    private void handleConnect() {
        String username = usernameField.getText().trim();
        if (username.isEmpty() || !Client.isUsernameValid(username)) {
            FxEventHandler.showAlert("Veuillez entrer un nom d'utilisateur valid.");
            return;
        }

        Client client = new Client();
        int res = Integer.parseInt(client.sendRecString(username));
        System.out.println(res);

        if (res == 1) {
            loadNextPage();
        } else {
            FxEventHandler.showAlert("Le nom d'utilisateur est deja utiliser.");
        }
    }

    private void loadNextPage() {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader
                    .load(getClass().getResource("/com/fahchouch/client/main/main.fxml"));
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            javafx.stage.Stage stage = (javafx.stage.Stage) connectButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
