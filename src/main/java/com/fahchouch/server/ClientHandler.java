package com.fahchouch.server;

import com.fahchouch.shared.Packet;
import com.fahchouch.shared.SimpleClient;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler extends Thread {
    private Socket socket;
    private Server server;
    private ObjectInputStream objIn;
    private ObjectOutputStream objOut;
    private ClientServer client;

    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            objOut = new ObjectOutputStream(socket.getOutputStream());
            objOut.flush();
            objIn = new ObjectInputStream(socket.getInputStream());

            Packet loginPacket = (Packet) objIn.readObject();
            String username = loginPacket.getName();
            System.out.println("Login attempt: " + username);

            Packet response;
            synchronized (server) {
                if (server.findClientByUsername(username) == null) {
                    client = new ClientServer(username, server.getNextId(), socket, objOut);
                    server.addClient(client);
                    response = new Packet("response", "1");
                    server.showClients();
                } else {
                    response = new Packet("response", "0");
                }
            }
            objOut.writeObject(response);
            objOut.flush();

            if ("0".equals(response.getContent())) {
                closeConnection();
                return;
            }

            while (true) {
                Packet packet = (Packet) objIn.readObject();
                String query = packet.getContent();
                System.out.println(packet.getName());
                switch (packet.getName()) {
                    case "searchClient":
                        ArrayList<SimpleClient> clientsFound = server.searchClientsByUsername(query);
                        Packet result = new Packet("searchResult", new ArrayList<>(clientsFound));
                        objOut.writeObject(result);
                        objOut.flush();
                        break;
                    case "getUserRooms":
                        objOut.writeObject(
                                new Packet("getUserRoomsResult", server.getUserRoomsByUsername(packet.getContent())));
                        objOut.flush();
                    case "createPrivateRoom":
                        ClientServer other = server.findClientByUsername(query);
                        if (other != null && !other.equals(client)) {
                            server.getOrCreatePrivateRoom(client, other);
                        }
                        break;
                }

            }

        } catch (Exception e) {
            System.out.println("Client disconnected: " + (client != null ? client.getUsername() : "unknown"));
        } finally {
            server.removeClient(client);
            server.removeHandler(this);
            closeConnection();
        }
    }

    private void closeConnection() {
        try {
            if (objIn != null)
                objIn.close();
        } catch (Exception ignored) {
        }
        try {
            if (objOut != null)
                objOut.close();
        } catch (Exception ignored) {
        }
        try {
            if (socket != null && !socket.isClosed())
                socket.close();
        } catch (Exception ignored) {
        }
    }

    public ClientServer getClient() {
        return client;
    }

    public void sendPacket(Packet packet) {
        try {
            objOut.writeObject(packet);
            objOut.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
