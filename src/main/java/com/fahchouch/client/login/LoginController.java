package com.fahchouch.client.login;

import com.fahchouch.client.Client;
// import com.fahchouch.client.Client;
import com.fahchouch.client.fx.FxEventHandler;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public class LoginController {

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
        if (!username.isEmpty()) {
            Client client = new Client();
            client.sendRecString(username);
        } else {
            FxEventHandler.showAlert("Veuillez entrer un nom d'utilisateur.");
        }
    }
}
