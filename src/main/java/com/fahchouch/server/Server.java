package com.fahchouch.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private int port;
    private ArrayList<ClientServer> clients;

    public static void main(String[] args) {
        new Server(3001).runServer();
    }

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
    }

    public void runServer() {
        try (ServerSocket ss = new ServerSocket(port)) {
            System.out.println("server running on port : " + ss.getLocalPort());
            while (true) {
                Socket s = ss.accept();
                ClientServer c = new ClientServer(s);
                new ClientHandler(c, this).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ClientServer findClientByUsername(String username) {
        System.out.println("searching client");
        for (ClientServer client : clients) {
            if (client.getUsername().equals(username)) {
                System.out.println("client found");
                return client;
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
        for (ClientServer client : clients) {
            System.out.println(client.getUsername());
        }
    }
}
