package com.fahchouch.client.controllers.main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class MainPageController {

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private Button publicRoomButton;

    @FXML
    private ListView<String> usersListView;

    @FXML
    public void initialize() {
        searchField.setOnAction(e -> searchUserOrRoom());
        searchButton.setOnAction(e -> searchUserOrRoom());

        publicRoomButton.setOnAction(e -> joinPublicRoom());
    }

    private void searchUserOrRoom() {
        String query = searchField.getText().trim();
        if (!query.isEmpty() && query.length() > 2) {
            System.out.println("Searching for: " + query);
            usersListView.setVisible(true);
            usersListView.setManaged(true);
        }
    }

    private void joinPublicRoom() {
        System.out.println("🟢 Rejoint le salon public !");
    }
}
