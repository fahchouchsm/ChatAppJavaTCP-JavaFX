package com.fahchouch.client.controllers.chat;

import com.fahchouch.client.Client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class ChatPageController {
    private Client client;
    private String recipient;

    @FXML
    private ListView<String> chatList;

    @FXML
    private TextField messageField;

    @FXML
    private Button sendButton;

    public void setClient(Client client) {
        this.client = client;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    @FXML
    public void initialize() {
        sendButton.setOnAction(e -> sendMessage());
    }

    private void sendMessage() {
        String msg = messageField.getText().trim();
        if (msg.isEmpty())
            return;

        chatList.getItems().add("Moi: " + msg);

        messageField.clear();
    }
}
