package com.fahchouch.client.controllers.main;

import com.fahchouch.client.Client;
import com.fahchouch.shared.Packet;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class MainPageController {

    private Client client;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private Button publicRoomButton;

    @FXML
    private ListView<String> usersListView;

    public void setClient(Client client) {
        this.client = client;
    }

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
            String name;
            if (Character.isDigit(query.charAt(0))) {
                name = "searchRoom";
            } else {
                name = "searchClient";
            }
            Packet packet = new Packet(name, query);
            client.sendObject(packet);
            usersListView.setVisible(true);
            usersListView.setManaged(true);
        }
    }

    private void joinPublicRoom() {
        System.out.println("🟢 Rejoint le salon public !");
    }
}
