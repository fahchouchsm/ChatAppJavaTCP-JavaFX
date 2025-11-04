package com.fahchouch.client.controllers.main;

import java.util.ArrayList;

import com.fahchouch.client.Client;
import com.fahchouch.client.controllers.chat.ChatPageController;
import com.fahchouch.shared.Packet;
import com.fahchouch.shared.SimpleClient;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

        usersListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selectedUser = usersListView.getSelectionModel().getSelectedItem();
                if (selectedUser != null && !selectedUser.equals("Aucun résultat")) {
                    openChat(selectedUser);
                }
            }
        });
    }

    private void openChat(String username) {
        try {

            Packet packet = new Packet("createPrivateRoom", username);
            Packet res = (Packet) client.sendObject(packet);

            if (res != null && res.getName().equals("roomCreated")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fahchouch/client/chat/ChatPage.fxml"));
                javafx.scene.Parent root = loader.load();

                ChatPageController chatController = loader.getController();
                chatController.setClient(client);
                chatController.setRecipient(username);

                javafx.stage.Stage stage = new javafx.stage.Stage();
                stage.setTitle("Chat avec " + username);
                stage.setScene(new javafx.scene.Scene(root));
                stage.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void searchUserOrRoom() {
        String query = searchField.getText().trim();
        if (query.isEmpty())
            return;

        String name = Character.isDigit(query.charAt(0)) ? "searchRoom" : "searchClient";

        Packet packet = new Packet(name, query);
        Packet res = (Packet) client.sendObject(packet);

        if (res == null || res.getArrlist() == null || res.getArrlist().isEmpty()) {
            usersListView.setItems(FXCollections.observableArrayList("Aucun résultat"));
        } else {
            @SuppressWarnings("unchecked")
            ArrayList<SimpleClient> clientsRes = (ArrayList<SimpleClient>) res.getArrlist();

            ObservableList<String> usernames = FXCollections.observableArrayList();
            for (SimpleClient sc : clientsRes) {
                usernames.add(sc.getUsername());
            }
            usersListView.setItems(usernames);
        }

        usersListView.setVisible(true);
        usersListView.setManaged(true);
    }

    private void joinPublicRoom() {
        System.out.println("🟢 Rejoint le salon public !");
    }
}
