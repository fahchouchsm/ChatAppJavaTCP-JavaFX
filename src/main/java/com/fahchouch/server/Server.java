package com.fahchouch.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Server {
    private int port;
    private final List<ClientServer> clients = Collections.synchronizedList(new ArrayList<>());
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
