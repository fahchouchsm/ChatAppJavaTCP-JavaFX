package com.fahchouch.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class ClientHandler extends Thread {
    private ClientServer client;
    private BufferedReader in;
    private PrintWriter out;
    private Server server;

    public ClientHandler(ClientServer client, Server server) {
        try {
            this.client = client;
            this.server = server;
            in = new BufferedReader(new InputStreamReader(this.client.getSocket().getInputStream()));
            out = new PrintWriter(this.client.getSocket().getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            String usernamereq;
            while ((usernamereq = in.readLine()) != null) {
                System.out.println("waiting for the client username");
                if (server.findClientByUsername(usernamereq) == null) {
                    client.setUsername(usernamereq);
                    server.addClient(client);
                    out.println(1);
                    out.flush();
                    server.showClients();
                } else {
                    out.println(0);
                    out.flush();
                }
            }

            System.out.println("client disconnected: " + client.getUsername());

        } catch (Exception e) {
            System.out.println("Error with client " + client.getUsername() + ": " + e.getMessage());
        } finally {
            cleanUp();
        }
    }

    public void cleanUp() {
        try {
            server.removeClient(client);
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
