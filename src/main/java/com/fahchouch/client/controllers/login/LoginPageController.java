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
            if (event.getCode() == KeyCode.ENTER) {
                handleConnect();
            }
        });
    }

    @FXML
    private void handleConnect() {
        String username = usernameField.getText().trim().toLowerCase();

        if (username.isEmpty() || !Client.isUsernameValid(username)) {
            FxEventHandler.showAlert("Veuillez entrer un nom d'utilisateur valide.");
            return;
        }

        connectButton.setDisable(true);

        try {
            client = new Client();
            int res = client.login(username);
            System.out.println("Login result: " + res);

            if (res == 1) {
                client.setUsername(username);
                loadNextPage();
            } else {
                FxEventHandler.showAlert("Le nom d'utilisateur est déjà utilisé.");
            }
        } catch (Exception e) {
            FxEventHandler.showAlert("Erreur de connexion au serveur !");
            e.printStackTrace();
        } finally {
            connectButton.setDisable(false);
        }
    }

    private void loadNextPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fahchouch/client/main/main.fxml"));
            javafx.scene.Parent root = loader.load();

            MainPageController mainController = loader.getController();
            mainController.setClient(client);

            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            javafx.stage.Stage stage = (javafx.stage.Stage) connectButton.getScene().getWindow();
            stage.setResizable(true);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            FxEventHandler.showAlert("Impossible de charger la page principale !");
        }
    }

}
