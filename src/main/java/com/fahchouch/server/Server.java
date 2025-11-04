package com.fahchouch.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fahchouch.server.Room.ChatRoom;
import com.fahchouch.shared.SimpleClient;

public class Server {
    private int port;
    private final List<ClientServer> clients = Collections.synchronizedList(new ArrayList<>());
    private int idCounter = 0;
    private final List<ChatRoom> chatRooms = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        new Server(3001).runServer();
    }

    public ChatRoom getOrCreatePrivateRoom(ClientServer a, ClientServer b) {
        String roomName = a.getUsername() + "_" + b.getUsername();

        synchronized (chatRooms) {
            for (ChatRoom room : chatRooms) {
                if (room.getName().equals(roomName) || room.getName().equals(b.getUsername() + "_" + a.getUsername())) {
                    return room;
                }
            }

            ChatRoom newRoom = new ChatRoom(roomName);
            newRoom.addParticipant(a);
            newRoom.addParticipant(b);
            chatRooms.add(newRoom);
            return newRoom;
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
                new ClientHandler(s, this).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized int getNextId() {
        return idCounter++;
    }

    public ClientServer findClientByUsername(String username) {
        if (username == null)
            return null;
        synchronized (clients) {
            return clients.stream().filter(c -> username.equals(c.getUsername())).findFirst().orElse(null);
        }
    }

    public List<SimpleClient> searchClientsByUsername(String query) {
        if (query == null || query.isEmpty())
            return Collections.emptyList();

        List<SimpleClient> result = new ArrayList<>();
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

    public void addClient(ClientServer client) {
        clients.add(client);
    }

    public void removeClient(ClientServer client) {
        clients.remove(client);
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
