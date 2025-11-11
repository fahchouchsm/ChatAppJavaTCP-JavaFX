package com.fahchouch.client.controllers.chat;

import com.fahchouch.client.Client;
import com.fahchouch.shared.Packet;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;

public class ChatPageController {

    private Client client;
    private String roomName;

    @FXML
    private Label chatTitleLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox messagesBox;
    @FXML
    private TextField messageField;
    @FXML
    private Button sendButton;
    @FXML
    private Button fileButton;

    public String getRoomName() {
        return roomName;
    }

    public Client getClient() {
        return client;
    }

    public void setRoom(String roomId) {
        this.roomName = roomId;
    }

    public void setClient(Client client) {
        this.client = client;
        ChatWindowManager.register(this);
        client.setOnRoomHistory(this::loadHistory);
        client.setOnFileReceived(this::handleFileReceived);
    }

    private void loadHistory(ArrayList<Object> history) {
        Platform.runLater(() -> {
            messagesBox.getChildren().clear();
            for (Object entry : history) {
                @SuppressWarnings("unchecked")
                ArrayList<String> msg = (ArrayList<String>) entry;
                String type = msg.size() > 2 ? msg.get(2) : "text";
                if ("file".equals(type)) {
                    String sender = msg.get(0);
                    String filename = msg.get(1);
                    boolean isMe = sender.equals(client.getUsername());
                    appendFileMessage(isMe ? "Moi" : sender, filename, isMe);
                } else {
                    String sender = msg.get(0);
                    String text = msg.get(1);
                    boolean isMe = sender.equals(client.getUsername());
                    appendMessage(isMe ? "Moi" : sender, text, isMe);
                }
            }
        });
    }

    public void initializeUI(String title) {
        Platform.runLater(() -> {
            chatTitleLabel.setText(title);
            statusLabel.setText("En ligne");

            messageField.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ENTER)
                    sendMessage();
            });
            sendButton.setOnAction(e -> sendMessage());
            fileButton.setOnAction(e -> openFileChooser());

            client.requestHistory(roomName);
        });
    }

    @FXML
    private void openFileChooser() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choisir un fichier");
        File file = chooser.showOpenDialog(null);
        if (file != null) {
            sendFile(file);
        }
    }

    private void sendFile(File file) {
        Platform.runLater(() -> {
            try {
                byte[] bytes = Files.readAllBytes(file.toPath());
                String filename = file.getName();
                String base64 = Base64.getEncoder().encodeToString(bytes);

                ArrayList<String> data = new ArrayList<>();
                data.add(roomName);
                data.add(client.getUsername());
                data.add("file:" + filename + ":" + base64);

                client.sendObject(new Packet("file", null, data));
                appendFileMessage("Moi", filename, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void handleFileReceived(Packet packet) {
        ArrayList<?> data = packet.getArrlist();
        if (data.size() < 3)
            return;
        String room = (String) data.get(0);
        String sender = (String) data.get(1);
        String payload = (String) data.get(2);

        if (!room.equals(roomName))
            return;

        if (payload.startsWith("file:")) {
            String[] parts = payload.substring(5).split(":", 2);
            String filename = parts[0];
            boolean isMe = sender.equals(client.getUsername());
            Platform.runLater(() -> appendFileMessage(isMe ? "Moi" : sender, filename, isMe));
        }
    }

    private void appendFileMessage(String sender, String filename, boolean isMe) {
        Platform.runLater(() -> {
            VBox bubbleBox = new VBox(5);
            bubbleBox.setMaxWidth(300);
            bubbleBox.setPadding(new Insets(8, 12, 8, 12));
            bubbleBox.setStyle(isMe
                    ? "-fx-background-color: #5c6bc0; -fx-background-radius: 12;"
                    : "-fx-background-color: #3a3a50; -fx-background-radius: 12;");

            Label senderLabel = new Label(sender + ":");
            senderLabel.setTextFill(isMe ? Color.LIGHTGRAY : Color.CYAN);
            senderLabel.setFont(Font.font(12));

            HBox fileBox = new HBox(8);
            fileBox.setAlignment(Pos.CENTER_LEFT);

            Label fileIcon = new Label("File");
            fileIcon.setStyle("-fx-background-color: #ffffff22; -fx-padding: 5; -fx-background-radius: 5;");

            Label fileLabel = new Label(filename);
            fileLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

            Button downloadBtn = new Button("Download");
            downloadBtn.setStyle("-fx-background-color: #43a047; -fx-text-fill: white; -fx-background-radius: 15;");
            downloadBtn.setOnAction(e -> downloadFile(filename));

            fileBox.getChildren().addAll(fileIcon, fileLabel, downloadBtn);
            bubbleBox.getChildren().addAll(senderLabel, fileBox);

            HBox container = new HBox(bubbleBox);
            container.setAlignment(isMe ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
            container.setMaxWidth(Double.MAX_VALUE);

            messagesBox.getChildren().add(container);
            scrollToBottom();
        });
    }

    private void downloadFile(String filename) {
        // TODO: Save file from server or cache
        System.out.println("Download: " + filename);
    }

    @FXML
    private void sendMessage() {
        String msg = messageField.getText().trim();
        if (msg.isEmpty() || roomName == null)
            return;

        ArrayList<String> data = new ArrayList<>();
        data.add(roomName);
        data.add(client.getUsername());
        data.add(msg);

        client.sendObject(new Packet("message", null, data));
        messageField.clear();
        appendMessage("Moi", msg, true);
    }

    public void appendMessage(String sender, String msg, boolean isMe) {
        Platform.runLater(() -> {
            VBox bubbleBox = new VBox(2);
            bubbleBox.setMaxWidth(300);
            bubbleBox.setPadding(new Insets(8, 12, 8, 12));
            bubbleBox.setStyle(isMe
                    ? "-fx-background-color: #5c6bc0; -fx-background-radius: 12; -fx-text-fill: white;"
                    : "-fx-background-color: #3a3a50; -fx-background-radius: 12; -fx-text-fill: white;");

            Label senderLabel = new Label(sender + ":");
            senderLabel.setFont(Font.font(12));
            senderLabel.setTextFill(isMe ? Color.LIGHTGRAY : Color.CYAN);

            Label msgLabel = new Label(msg);
            msgLabel.setWrapText(true);
            msgLabel.setStyle("-fx-font-size: 14; -fx-text-fill: white;");

            bubbleBox.getChildren().addAll(senderLabel, msgLabel);

            HBox container = new HBox(bubbleBox);
            container.setMaxWidth(Double.MAX_VALUE);
            container.setAlignment(isMe ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

            messagesBox.getChildren().add(container);
            scrollToBottom();
        });
    }

    private void scrollToBottom() {
        scrollPane.layout();
        scrollPane.setVvalue(1.0);
    }
}