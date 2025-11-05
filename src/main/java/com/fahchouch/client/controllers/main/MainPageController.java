package com.fahchouch.client.controllers.main;

import java.util.ArrayList;

import com.fahchouch.client.Client;
import com.fahchouch.client.ClientRunner;
import com.fahchouch.client.controllers.chat.ChatPageController;
import com.fahchouch.shared.Packet;
import com.fahchouch.shared.SimpleClient;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
    @FXML
    private ListView<String> roomsListView;
    @FXML
    private Button logoutButton;
    @FXML
    private Label usernameLabel;

    public void initAfterLogin() {
        usernameLabel.setText(client.getUsername());
        loadUserRooms();
    }

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

        roomsListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selectedRoom = roomsListView.getSelectionModel().getSelectedItem();
                if (selectedRoom != null && !selectedRoom.equals("Aucun salon")) {
                    openRoomChat(selectedRoom);
                }
            }
        });

        logoutButton.setOnAction(e -> logout());
    }

    private void loadUserRooms() {
        if (client == null)
            return;

        client.sendObject(new Packet("getUserRooms", client.getUsername()));
        Packet res = (Packet) client.receiveObject();

        if (res == null || res.getArrlist() == null || res.getArrlist().isEmpty()) {
            roomsListView.setItems(FXCollections.observableArrayList("Aucun salon"));
        } else {
            @SuppressWarnings("unchecked")
            ArrayList<String> rooms = (ArrayList<String>) res.getArrlist();
            ObservableList<String> roomNames = FXCollections.observableArrayList(rooms);
            roomsListView.setItems(roomNames);
        }
    }

    private void searchUserOrRoom() {
        String query = searchField.getText().trim();
        if (query.isEmpty())
            return;

        String name = Character.isDigit(query.charAt(0)) ? "searchRoom" : "searchClient";
        client.sendObject(new Packet(name, query));
        Packet res = (Packet) client.receiveObject();

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
    }

    private void openChat(String username) {
        try {
            client.sendObject(new Packet("createPrivateRoom", username));

            FXMLLoader loader = new FXMLLoader(ClientRunner.class.getResource("/com/fahchouch/client/chat/chat.fxml"));
            Parent root = loader.load();
            ChatPageController ctrl = loader.getController();
            ctrl.setClient(client);
            ctrl.setRecipient(username);

            Stage stage = new Stage();
            stage.setTitle("Chat avec " + username);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openRoomChat(String roomName) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getClassLoader().getResource("com/fahchouch/client/chat/chat.fxml"));
            javafx.scene.Parent root = loader.load();
            ChatPageController chatController = loader.getController();
            chatController.setClient(client);
            chatController.setRoom(roomName);

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Salon: " + roomName);
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void joinPublicRoom() {
        openRoomChat("Public");
        System.out.println("🟢 Rejoint le salon public !");
    }

    private void logout() {
        System.out.println("👋 User logged out!");

    }
}