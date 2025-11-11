package com.fahchouch.server;

import com.fahchouch.shared.Packet;
import com.fahchouch.shared.SimpleClient;
import com.fahchouch.shared.chat.Room;

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
                String query = (String) packet.getContent();
                System.out.println("Packet: " + packet.getName());
                switch (packet.getName()) {
                    case "searchClient" -> {
                        ArrayList<SimpleClient> clientsFound = server.searchClientsByUsername(query);
                        objOut.writeObject(new Packet("searchResult", null, new ArrayList<>(clientsFound)));
                        objOut.flush();
                    }
                    case "getUserRooms" -> {
                        ArrayList<Object> roomsData = server.getUserRoomsByUsername(query);
                        objOut.writeObject(new Packet("getUserRoomsResult", null, roomsData));
                        objOut.flush();
                    }
                    case "createPrivateRoom" -> {
                        ClientServer other = server.findClientByUsername(query);
                        if (other != null && !other.equals(client)) {
                            server.getOrCreatePrivateRoom(client, other);
                        }
                    }
                    case "message" -> {
                        ArrayList<?> data = packet.getArrlist();
                        String roomName = (String) data.get(0);
                        String sender = (String) data.get(1);
                        String msg = (String) data.get(2);
                        Room room = server.findRoomByName(roomName);
                        if (room != null) {
                            room.broadcastMessage(sender, msg);
                        }
                    }
                    case "getHistory" -> {
                        String roomName = (String) packet.getContent();
                        ArrayList<String[]> history = server.getRoomHistory(roomName);
                        ArrayList<Object> data = new ArrayList<>();
                        for (String[] msg : history) {
                            ArrayList<String> entry = new ArrayList<>();
                            entry.add(msg[0]);
                            entry.add(msg[1]);
                            data.add(entry);
                        }
                        objOut.writeObject(new Packet("roomHistory", null, data));
                        objOut.flush();
                    }
                    case "file" -> {
                        ArrayList<?> data = packet.getArrlist();
                        String roomName = (String) data.get(0);
                        String sender = (String) data.get(1);
                        String payload = (String) data.get(2);

                        Room room = server.findRoomByName(roomName);
                        if (room != null) {
                            room.broadcastFile(sender, payload);
                        }
                    }
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
}