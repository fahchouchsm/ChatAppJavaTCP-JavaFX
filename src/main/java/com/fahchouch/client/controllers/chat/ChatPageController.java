package com.fahchouch.client.controllers.chat;

import com.fahchouch.client.Client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;

public class ChatPageController {

    private Client client;
    private String recipient;
    private String roomName;
    private boolean isRoom = false;

    @FXML
    private Label chatTitleLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private TextArea chatArea;
    @FXML
    private TextField messageField;
    @FXML
    private Button sendButton;

    public void setClient(Client client) {
        this.client = client;
        // Handle incoming messages live
        client.setOnMessageReceived(msg -> {
            Platform.runLater(() -> chatArea.appendText(msg + "\n"));
        });
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
        messageField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                sendMessage();
        });
    }

    @FXML
    private void sendMessage() {
        String msg = messageField.getText().trim();
        if (msg.isEmpty())
            return;

        String target = isRoom ? roomName : recipient;
        client.sendMessage(target, msg);
        chatArea.appendText("Moi: " + msg + "\n");
        messageField.clear();
    }
}
