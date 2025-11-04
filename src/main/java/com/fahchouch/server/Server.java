package com.fahchouch.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Server {
    private int port;

    private final List<ClientServer> clients = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        new Server(3001).runServer();
    }

    public Server(int port) {
        this.port = port;
    }

    public void runServer() {
        try (ServerSocket ss = new ServerSocket(port)) {
            System.out.println("server running on port : " + ss.getLocalPort());
            while (true) {
                Socket s = ss.accept();

                new ClientHandler(s, this).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ClientServer findClientByUsername(String username) {
        if (username == null)
            return null;
        synchronized (clients) {
            for (ClientServer client : clients) {
                if (username.equals(client.getUsername())) {
                    return client;
                }
            }
        }
        return null;
    }

    public void addClient(ClientServer c) {
        clients.add(c);
    }

    public void removeClient(ClientServer c) {
        clients.remove(c);
    }

    public void showClients() {
        System.out.println("-------------------");
        System.out.println("Available Clients :");
        synchronized (clients) {
            for (ClientServer client : clients) {
                System.out.println(client.getUsername());
            }
        }
    }
}
