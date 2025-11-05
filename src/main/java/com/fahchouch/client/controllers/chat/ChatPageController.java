package com.fahchouch.client.controllers.chat;

import com.fahchouch.client.Client;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ChatPageController {

    private Client client;
    private String recipient;
    private String roomName;
    private boolean isRoom = false;

    @FXML
    private Label chatTitleLabel;
    @FXML
    private Label statusLabel;

    public void setClient(Client client) {
        this.client = client;
    }

    public void setRecipient(String username) {
        this.recipient = username;
        this.isRoom = false;
        initChat("Chat avec " + username);
    }

    public void setRoom(String roomName) {
        this.roomName = roomName;
        this.isRoom = true;
        initChat("Salon: " + roomName);
    }

    private void initChat(String title) {
        chatTitleLabel.setText(title);
        statusLabel.setText(isRoom ? "Groupe" : "En ligne");

    }
}