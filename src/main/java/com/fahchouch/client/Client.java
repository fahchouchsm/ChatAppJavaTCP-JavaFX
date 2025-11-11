package com.fahchouch.client;

import com.fahchouch.shared.Packet;
import com.fahchouch.shared.SimpleClient;
import javafx.application.Platform;
import com.fahchouch.client.fx.FxEventHandler;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Client {
    private Socket socket;
    private ObjectOutputStream objOut;
    private ObjectInputStream objIn;
    private String username;
    private final Thread packetListener = new Thread(this::listenForPackets);
    private Consumer<ArrayList<Object>> onRoomHistory;
    private Consumer<String> onRoomCreated;
    private Consumer<Packet> onMessageReceived;
    private Consumer<ArrayList<SimpleClient>> onSearchResult;
    private Consumer<ArrayList<Object>> onGetUserRoomsResult;

    private Consumer<Packet> onFileReceived;

    public void setOnFileReceived(Consumer<Packet> callback) {
        this.onFileReceived = callback;
    }

    public void setOnRoomHistory(Consumer<ArrayList<Object>> callback) {
        this.onRoomHistory = callback;
    }

    public void setOnRoomCreated(Consumer<String> callback) {
        this.onRoomCreated = callback;
    }

    public void setOnMessageReceived(Consumer<Packet> callback) {
        this.onMessageReceived = callback;
    }

    public void setOnSearchResult(Consumer<ArrayList<SimpleClient>> callback) {
        this.onSearchResult = callback;
    }

    public void setOnGetUserRoomsResult(Consumer<ArrayList<Object>> callback) {
        this.onGetUserRoomsResult = callback;
    }

    public void startListening() {
        packetListener.setDaemon(true);
        packetListener.start();
    }

    public Client() {
        try {
            socket = new Socket("localhost", 3001);
            objOut = new ObjectOutputStream(socket.getOutputStream());
            objOut.flush();
            objIn = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
            FxEventHandler.showAlert("Impossible de se connecter au serveur");
        }
    }

    private void listenForPackets() {
        try {
            while (true) {
                Object obj = objIn.readObject();
                if (obj instanceof Packet packet) {
                    switch (packet.getName()) {
                        case "roomCreated" -> {
                            String roomId = (String) packet.getContent();
                            if (onRoomCreated != null) {
                                Platform.runLater(() -> onRoomCreated.accept(roomId));
                            }
                        }
                        case "searchResult" -> {
                            @SuppressWarnings("unchecked")
                            ArrayList<SimpleClient> results = (ArrayList<SimpleClient>) packet.getArrlist();
                            if (onSearchResult != null) {
                                Platform.runLater(
                                        () -> onSearchResult.accept(results != null ? results : new ArrayList<>()));
                            }
                        }
                        case "getUserRoomsResult" -> {
                            @SuppressWarnings("unchecked")
                            ArrayList<Object> roomsData = (ArrayList<Object>) packet.getArrlist();
                            if (onGetUserRoomsResult != null) {
                                Platform.runLater(() -> onGetUserRoomsResult
                                        .accept(roomsData != null ? roomsData : new ArrayList<>()));
                            }
                        }
                        case "message" -> {
                            if (onMessageReceived != null) {
                                Platform.runLater(() -> onMessageReceived.accept(packet));
                            }
                        }
                        case "roomHistory" -> {
                            @SuppressWarnings("unchecked")
                            ArrayList<Object> history = (ArrayList<Object>) packet.getArrlist();
                            if (onRoomHistory != null) {
                                Platform.runLater(() -> onRoomHistory.accept(history));
                            }
                        }
                        case "file" -> {
                            if (onFileReceived != null) {
                                Platform.runLater(() -> onFileReceived.accept(packet));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Server disconnected");
        }
    }

    public void requestHistory(String roomName) {
        sendObject(new Packet("getHistory", roomName));
    }

    public int login(String username) {
        try {
            Packet packet = new Packet(username);
            objOut.writeObject(packet);
            objOut.flush();
            Packet response = (Packet) objIn.readObject();
            if (response != null && "1".equals(response.getContent())) {
                this.username = username;
                return 1;
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void sendObject(Object obj) {
        try {
            objOut.writeObject(obj);
            objOut.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static boolean isUsernameValid(String username) {
        if (username == null || username.isEmpty() || username.length() < 3)
            return false;
        return !Character.isDigit(username.charAt(0));
    }

    public void close() {
        try {
            socket.close();
        } catch (Exception ignored) {
        }
    }
}