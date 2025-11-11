package com.fahchouch.server;

import com.fahchouch.shared.Packet;
import com.fahchouch.shared.SimpleClient;
import com.fahchouch.shared.chat.Room;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private final int port;
    private final ArrayList<ClientServer> clients = new ArrayList<>();
    private final ArrayList<ClientHandler> handlers = new ArrayList<>();
    private final ArrayList<Room> rooms = new ArrayList<>();
    private int idCounter = 0;

    public static void main(String[] args) {
        new Server(3001).runServer();
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
                synchronized (handlers) {
                    handlers.add(handler);
                }
                handler.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Room getOrCreatePrivateRoom(ClientServer a, ClientServer b) {
        synchronized (rooms) {
            for (Room room : rooms) {
                ArrayList<ClientServer> parts = room.getParticipants();
                if (parts.contains(a) && parts.contains(b))
                    return room;
            }
            Room newRoom = new Room();
            newRoom.addParticipant(a);
            newRoom.addParticipant(b);
            rooms.add(newRoom);
            notifyRoomCreated(a, b, newRoom.getName());
            return newRoom;
        }
    }

    private void notifyRoomCreated(ClientServer a, ClientServer b, String roomId) {
        Packet packet = new Packet("roomCreated", roomId);
        sendToClient(a, packet);
        sendToClient(b, packet);
    }

    public Room findRoomByName(String name) {
        synchronized (rooms) {
            for (Room room : rooms) {
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

    public ArrayList<String[]> getRoomHistory(String roomName) {
        Room room = findRoomByName(roomName);
        return room != null ? room.getMessageHistory() : new ArrayList<>();
    }

    public synchronized int getNextId() {
        return idCounter++;
    }

    public ClientServer findClientByUsername(String username) {
        if (username == null)
            return null;
        synchronized (clients) {
            for (ClientServer c : clients) {
                if (username.equals(c.getUsername()))
                    return c;
            }
        }
        return null;
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

    public ArrayList<Object> getUserRoomsByUsername(String username) {
        ArrayList<Object> result = new ArrayList<>();
        synchronized (rooms) {
            for (Room room : rooms) {
                boolean inRoom = false;
                ArrayList<String> names = new ArrayList<>();
                for (ClientServer p : room.getParticipants()) {
                    if (username.equals(p.getUsername()))
                        inRoom = true;
                    names.add(p.getUsername());
                }
                if (inRoom) {
                    ArrayList<Object> entry = new ArrayList<>();
                    entry.add(room.getName());
                    entry.add(names);
                    result.add(entry);
                }
            }
        }
        return result;
    }

    public void addClient(ClientServer client) {
        synchronized (clients) {
            clients.add(client);
        }
    }

    public void removeClient(ClientServer client) {
        if (client != null) {
            synchronized (clients) {
                clients.remove(client);
            }
        }
    }

    public void removeHandler(ClientHandler handler) {
        synchronized (handlers) {
            handlers.remove(handler);
        }
    }

    public void showClients() {
        System.out.println("Clients connected:");
        synchronized (clients) {
            for (ClientServer c : clients) {
                System.out.println(c.getUsername());
            }
        }
    }
}