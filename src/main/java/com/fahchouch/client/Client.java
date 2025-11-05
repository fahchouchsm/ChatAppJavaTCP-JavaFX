package com.fahchouch.client;

import com.fahchouch.shared.Packet;
import com.fahchouch.client.fx.FxEventHandler;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    private Socket socket;
    private ObjectOutputStream objOut;
    private ObjectInputStream objIn;
    private String username;

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

    // FIRE AND FORGET — NO RETURN
    public void sendObject(Object obj) {
        try {
            objOut.writeObject(obj);
            objOut.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // BLOCKING READ — ONLY WHEN NEEDED
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