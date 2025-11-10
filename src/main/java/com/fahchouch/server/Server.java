package com.fahchouch.server;

import com.fahchouch.shared.Packet;
import com.fahchouch.shared.SimpleClient;
import com.fahchouch.shared.chat.Room;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Server {
    private int port;
    private final List<ClientServer> clients = Collections.synchronizedList(new ArrayList<>());
    private final List<ClientHandler> handlers = Collections.synchronizedList(new ArrayList<>()); // ADD THIS
    private int idCounter = 0;
    private final List<Room> Rooms = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        new Server(3001).runServer();
    }

    public Room getOrCreatePrivateRoom(ClientServer a, ClientServer b) {
        String roomName = a.getUsername() + "_" + b.getUsername();
        for (Room room : Rooms) {
            if (room.getName().equals(roomName) || room.getName().equals(b.getUsername() + "_" + a.getUsername())) {
                return room;
            }
        }

        Room newRoom = new Room(roomName);
        newRoom.addParticipant(a);
        newRoom.addParticipant(b);
        Rooms.add(newRoom);

        notifyRoomCreated(a, b, newRoom.getName());
        return newRoom;
    }

    private void notifyRoomCreated(ClientServer a, ClientServer b, String roomName) {
        Packet packet = new Packet("roomCreated", roomName);
        sendToClient(a, packet);
        sendToClient(b, packet);
    }

    public Room findRoomByName(String name) {
        synchronized (Rooms) {
            for (Room room : Rooms) {
                if (room.getName().equals(name))
                    return room;
            }
        }
        return null;
    }

    private void sendToClient(ClientServer client, Packet packet) {
        if (client == null)
            return;
        try {
            client.getOutputStream().writeObject(packet);
            client.getOutputStream().flush();
        } catch (Exception e) {
            System.out.println("Failed to send to " + client.getUsername());
        }
    }

    public Server(int port) {
        this.port = port;
    }

    public void runServer() {
        try (ServerSocket ss = new ServerSocket(port)) {
            System.out.println("Server running on port " + port);
            while (true) {
                Socket s = ss.accept();
                ClientHandler handler = new ClientHandler(s, this);
                handlers.add(handler); // TRACK HANDLER
                handler.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ClientHandler> getActiveHandlers() {
        synchronized (handlers) {
            return new ArrayList<>(handlers);
        }
    }

    public void removeHandler(ClientHandler handler) {
        handlers.remove(handler);
    }

    public synchronized int getNextId() {
        return idCounter++;
    }

    public ClientServer findClientByUsername(String username) {
        if (username == null)
            return null;
        synchronized (clients) {
            return clients.stream()
                    .filter(c -> username.equals(c.getUsername()))
                    .findFirst()
                    .orElse(null);
        }
    }

    public ArrayList<SimpleClient> searchClientsByUsername(String query) {
        if (query == null || query.isEmpty())
            return null;

        ArrayList<SimpleClient> result = new ArrayList<>();
        String lowerQuery = query.toLowerCase();

        synchronized (clients) {
            for (ClientServer c : clients) {
                String username = c.getUsername();
                if (username != null && username.toLowerCase().contains(lowerQuery)) {
                    result.add(new SimpleClient(username, c.getId()));
                }
            }
        }
        return result;
    }

    public ArrayList<String> getUserRoomsByUsername(String username) {
        ArrayList<String> result = new ArrayList<>();
        synchronized (Rooms) {
            for (Room room : Rooms) {
                for (ClientServer p : room.getParticipants()) {
                    if (username.equals(p.getUsername())) {
                        result.add(room.getName());
                        break;
                    }
                }
            }
        }
        return result;
    }

    public void addClient(ClientServer client) {
        clients.add(client);
    }

    public void removeClient(ClientServer client) {
        if (client != null) {
            clients.remove(client);
        }
    }

    public List<ClientServer> getClientsSnapshot() {
        synchronized (clients) {
            return new ArrayList<>(clients);
        }
    }

    public void showClients() {
        System.out.println("Clients connected:");
        synchronized (clients) {
            clients.forEach(c -> System.out.println(c.getUsername()));
        }
    }
}