package com.fahchouch.client.controllers.main;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class MainPageController {

    @FXML
    private TextField searchField;

    @FXML
    private ListView<String> usersListView;

    @FXML
    public void initialize() {

        searchField.setOnAction(e -> searchUserOrRoom());
    }

    private void searchUserOrRoom() {
        String query = searchField.getText().trim();
        if (!query.isEmpty()) {
            System.out.println("Searching for: " + query);
            // todo
        }
    }
}
