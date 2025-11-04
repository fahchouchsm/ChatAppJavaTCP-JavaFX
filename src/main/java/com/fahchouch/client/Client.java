package com.fahchouch.client;

import com.fahchouch.shared.Packet;
import com.fahchouch.shared.SimpleClient;
import com.fahchouch.client.fx.FxEventHandler;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client extends SimpleClient {
    private Socket socket;
    private ObjectOutputStream objOut;
    private ObjectInputStream objIn;

    public Client() {
        super(null, 0);
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
        return socket;
    }

    @Override
    public String getUsername() {
        return super.getUsername();
    }

    @Override
    public void setUsername(String username) {
        super.setUsername(username);
    }

    public static boolean isUsernameValid(String username) {
        if (username == null || username.isEmpty() || username.length() < 3)
            return false;
        return !Character.isDigit(username.charAt(0));
    }
}
