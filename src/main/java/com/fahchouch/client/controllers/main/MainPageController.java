package com.fahchouch.client.controllers.main;

import com.fahchouch.client.Client;
import com.fahchouch.client.ClientRunner;
import com.fahchouch.client.controllers.chat.ChatPageController;
import com.fahchouch.shared.Packet;
import com.fahchouch.shared.SimpleClient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.ArrayList;

public class MainPageController {

    private Client client;
    private String pendingChatUser = null;

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

    @FXML
    public void initialize() {
        searchField.setOnAction(e -> searchUserOrRoom());
        searchButton.setOnAction(e -> searchUserOrRoom());
        publicRoomButton.setOnAction(e -> joinPublicRoom());

        usersListView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                String user = usersListView.getSelectionModel().getSelectedItem();
                if (user != null && !user.equals("Aucun résultat"))
                    openChat(user);
            }
        });

        roomsListView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                String display = roomsListView.getSelectionModel().getSelectedItem();
                if (display != null && !display.equals("Aucun salon"))
                    openRoomChat(display);
            }
        });

        logoutButton.setOnAction(e -> logout());
    }

    private void searchUserOrRoom() {
        String q = searchField.getText().trim();
        if (q.isEmpty())
            return;
        String type = Character.isDigit(q.charAt(0)) ? "searchRoom" : "searchClient";
        client.sendObject(new Packet(type, q));
    }

    public void setClient(Client client) {
        this.client = client;
        client.setOnRoomCreated(this::handleRoomCreated);
        client.setOnSearchResult(this::updateUsersList);
        client.setOnGetUserRoomsResult(this::updateRoomsList);
    }

    private void updateUsersList(ArrayList<SimpleClient> clients) {
        if (clients == null || clients.isEmpty()) {
            usersListView.setItems(FXCollections.observableArrayList("Aucun résultat"));
        } else {
            ArrayList<String> names = new ArrayList<>();
            for (SimpleClient c : clients)
                names.add(c.getUsername());
            usersListView.setItems(FXCollections.observableArrayList(names));
        }
    }

    private void updateRoomsList(ArrayList<Object> roomsData) {
        if (roomsData == null || roomsData.isEmpty()) {
            roomsListView.setItems(FXCollections.observableArrayList("Aucun salon"));
            return;
        }

        ArrayList<String> display = new ArrayList<>();
        for (Object entry : roomsData) {
            @SuppressWarnings("unchecked")
            ArrayList<Object> roomInfo = (ArrayList<Object>) entry;
            String roomId = (String) roomInfo.get(0);
            @SuppressWarnings("unchecked")
            ArrayList<String> users = (ArrayList<String>) roomInfo.get(1);

            ArrayList<String> others = new ArrayList<>(users);
            others.remove(client.getUsername());
            String othersStr = others.isEmpty() ? "seul" : String.join(", ", others);

            display.add(roomId + " (" + othersStr + ")");
        }
        roomsListView.setItems(FXCollections.observableArrayList(display));
    }

    private void loadUserRooms() {
        if (client != null)
            client.sendObject(new Packet("getUserRooms", client.getUsername()));
    }

    private void openChat(String username) {
        this.pendingChatUser = username;
        client.sendObject(new Packet("createPrivateRoom", username));
    }

    private void handleRoomCreated(String roomId) {
        Platform.runLater(() -> {
            if (pendingChatUser == null) {
                loadUserRooms();
                return;
            }

            String user = pendingChatUser;
            pendingChatUser = null;

            try {
                FXMLLoader loader = new FXMLLoader(
                        ClientRunner.class.getResource("/com/fahchouch/client/chat/chat.fxml"));
                Parent root = loader.load();
                ChatPageController ctrl = loader.getController();

                ctrl.setRoom(roomId);
                ctrl.initializeUI("Chat avec " + user);
                ctrl.setClient(client);

                Stage stage = new Stage();
                stage.setTitle("Chat avec " + user);
                stage.setScene(new Scene(root));
                stage.show();

                loadUserRooms();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void openRoomChat(String displayText) {
        try {
            String roomId = displayText.split(" ")[0];

            FXMLLoader loader = new FXMLLoader(
                    ClientRunner.class.getResource("/com/fahchouch/client/chat/chat.fxml"));
            Parent root = loader.load();
            ChatPageController ctrl = loader.getController();

            ctrl.setRoom(roomId);
            ctrl.initializeUI("Salon: " + displayText);
            ctrl.setClient(client);

            Stage stage = new Stage();
            stage.setTitle("Salon: " + displayText);
            stage.setScene(new Scene(root));
            stage.show();

            ctrl.getClient().requestHistory(roomId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void joinPublicRoom() {
        openRoomChat("Public");
    }

    private void logout() {
        System.out.println("User logged out!");
    }
}