package com.fahchouch.client;

import com.fahchouch.shared.Packet;
import com.fahchouch.shared.SimpleClient;

import javafx.application.Platform;

import com.fahchouch.client.fx.FxEventHandler;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Client {
    private Socket socket;
    private ObjectOutputStream objOut;
    private ObjectInputStream objIn;
    private String username;
    private final Thread packetListener = new Thread(this::listenForPackets);
    private Runnable onRoomCreated;

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

    private Consumer<List<SimpleClient>> onSearchResult;

    public void setOnSearchResult(Consumer<List<SimpleClient>> callback) {
        this.onSearchResult = callback;
    }

    public void startListening() {
        packetListener.setDaemon(true);
        packetListener.start();
    }

    public void setOnRoomCreated(Runnable callback) {
        this.onRoomCreated = callback;
    }

    private void listenForPackets() {
        try {
            while (true) {
                Object obj = objIn.readObject();
                if (obj instanceof Packet packet) {
                    switch (packet.getName()) {
                        case "roomCreated" -> handleRoomCreated(packet.getContent());
                        case "searchResult" -> handleSearchResult((ArrayList<SimpleClient>) packet.getArrlist());
                        case "getUserRoomsResult" -> handleGetUserRoomsResult((ArrayList<String>) packet.getArrlist());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Server disconnected");
        }
    }

    private void handleSearchResult(ArrayList<SimpleClient> results) {
        if (onSearchResult != null) {
            Platform.runLater(() -> onSearchResult.accept(results != null ? results : new ArrayList<>()));
        }
    }

    private void handleGetUserRoomsResult(ArrayList<String> rooms) {
        if (onGetUserRoomsResult != null) {
            Platform.runLater(() -> onGetUserRoomsResult.accept(rooms != null ? rooms : new ArrayList<>()));
        }
    }

    private Consumer<List<String>> onGetUserRoomsResult;

    public void setOnGetUserRoomsResult(Consumer<List<String>> callback) {
        this.onGetUserRoomsResult = callback;
    }

    private void handleRoomCreated(String roomName) {
        if (onRoomCreated != null) {
            javafx.application.Platform.runLater(onRoomCreated);
        }
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

    public Object receiveObject() {
        try {
            return objIn.readObject();
        } catch (Exception e) {
            return null;
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Socket getSocket() {
        return socket;
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