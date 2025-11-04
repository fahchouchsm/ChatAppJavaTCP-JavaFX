package com.fahchouch.client.controllers.login;

import com.fahchouch.client.Client;
import com.fahchouch.client.controllers.main.MainPageController;
import com.fahchouch.client.fx.FxEventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public class LoginPageController {

    private Client client;

    @FXML
    private TextField usernameField;
    @FXML
    private Button connectButton;

    @FXML
    public void initialize() {
        usernameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                handleConnect();
        });
    }

    @FXML
    private void handleConnect() {
        String username = usernameField.getText().trim();
        if (username.isEmpty() || !Client.isUsernameValid(username)) {
            FxEventHandler.showAlert("Veuillez entrer un nom d'utilisateur valide.");
            return;
        }

        connectButton.setDisable(true);
        try {
            client = new Client();
            int res = client.login(username);
            if (res == 1) {
                client.setUsername(username);

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fahchouch/client/main/main.fxml"));
                javafx.scene.Parent root = loader.load();
                MainPageController m = loader.getController();
                m.setClient(client);
                javafx.scene.Scene scene = new javafx.scene.Scene(root);
                javafx.stage.Stage stage = (javafx.stage.Stage) connectButton.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } else {
                FxEventHandler.showAlert("Le nom d'utilisateur est déjà utilisé.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            FxEventHandler.showAlert("Erreur de connexion au serveur.");
        } finally {
            connectButton.setDisable(false);
        }
    }
}
