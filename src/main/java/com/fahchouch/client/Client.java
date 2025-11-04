package com.fahchouch.client;

import java.io.*;
import java.net.Socket;

import com.fahchouch.client.fx.FxEventHandler;
import com.fahchouch.server.Room.Packet;

public class Client {
    private Socket s;
    private String username;
    private ObjectOutputStream objOut;
    private ObjectInputStream objIn;

    public Client() {
        try {
            s = new Socket("localhost", 3001);
            objOut = new ObjectOutputStream(s.getOutputStream());
            objIn = new ObjectInputStream(s.getInputStream());
        } catch (IOException e) {
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
            return Integer.parseInt(response.getContent());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public Object sendObject(Object obj) {
        try {
            objOut.writeObject(obj);
            objOut.flush();
            return objIn.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Socket getSocket() {
        return s;
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

}
