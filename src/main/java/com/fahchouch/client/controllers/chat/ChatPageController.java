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

import java.io.ByteArrayInputStream;
import java.io.File;
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
                String sender = msg.get(0);
                String content = msg.get(1);
                boolean isMe = sender.equals(client.getUsername());

                if ("file".equals(type)) {
                    String[] parts = content.split(":", 3);
                    String filename = parts[0];
                    String base64 = parts.length > 1 ? parts[1] : "";
                    appendFileMessage(isMe ? "Moi" : sender, filename, base64, isMe);
                } else {
                    appendMessage(isMe ? "Moi" : sender, content, isMe);
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

            client.requestHistory(roomName);
        });
    }

    @FXML
    private void openFileChooser() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choisir un fichier");
        File file = chooser.showOpenDialog(null);
        if (file != null)
            sendFile(file);
    }

    private void sendFile(File file) {
        new Thread(() -> {
            try {
                byte[] bytes = Files.readAllBytes(file.toPath());
                String filename = file.getName();
                String base64 = Base64.getEncoder().encodeToString(bytes);

                ArrayList<String> data = new ArrayList<>();
                data.add(roomName);
                data.add(client.getUsername());
                data.add("file:" + filename + ":" + base64);

                client.sendObject(new Packet("file", null, data));

                Platform.runLater(() -> appendFileMessage("Moi", filename, base64, true));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void appendFileMessage(String sender, String filename, String base64, boolean isMe) {
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

        Label icon = new Label(isImage(filename) ? "Image" : "File");
        icon.setStyle(
                "-fx-background-color: #ffffff22; -fx-padding: 5; -fx-background-radius: 5; -fx-text-fill: white;");

        if (isImage(filename) && !base64.isEmpty()) {
            try {
                byte[] imgBytes = Base64.getDecoder().decode(base64);
                Image image = new Image(new ByteArrayInputStream(imgBytes));
                ImageView iv = new ImageView(image);
                iv.setFitWidth(200);
                iv.setPreserveRatio(true);
                fileBox.getChildren().add(iv);
            } catch (Exception e) {
                fileBox.getChildren().add(new Label("Image corrompue"));
            }
        } else {
            Label nameLabel = new Label(filename);
            nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
            Button download = new Button("Download");
            download.setStyle("-fx-background-color: #43a047; -fx-text-fill: white; -fx-background-radius: 15;");
            download.setOnAction(e -> saveFile(filename, base64));
            fileBox.getChildren().addAll(icon, nameLabel, download);
        }

        bubbleBox.getChildren().addAll(senderLabel, fileBox);

        HBox container = new HBox(bubbleBox);
        container.setAlignment(isMe ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        container.setMaxWidth(Double.MAX_VALUE);

        messagesBox.getChildren().add(container);
        scrollToBottom();
    }

    private boolean isImage(String filename) {
        String lower = filename.toLowerCase();
        return lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".gif");
    }

    private void saveFile(String filename, String base64) {
        FileChooser saver = new FileChooser();
        saver.setInitialFileName(filename);
        File file = saver.showSaveDialog(null);
        if (file != null) {
            try {
                byte[] data = Base64.getDecoder().decode(base64);
                Files.write(file.toPath(), data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
        Platform.runLater(() -> {
            scrollPane.layout();
            scrollPane.setVvalue(1.0);
        });
    }

    public void handleFileReceived(Packet packet) {
        ArrayList<?> data = packet.getArrlist();
        if (data.size() < 3)
            return;
        String room = (String) data.get(0);
        if (!room.equals(roomName))
            return;

        String sender = (String) data.get(1);
        if (sender.equals(client.getUsername()))
            return;

        String payload = (String) data.get(2);
        if (!payload.startsWith("file:"))
            return;

        String[] parts = payload.substring(5).split(":", 2);
        String filename = parts[0];
        String base64 = parts.length > 1 ? parts[1] : "";

        Platform.runLater(() -> appendFileMessage(sender, filename, base64, false));
    }
}