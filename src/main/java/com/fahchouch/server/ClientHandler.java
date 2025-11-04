package com.fahchouch.server;

import com.fahchouch.shared.Packet;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ClientHandler extends Thread {
    private ClientServer client;
    private Server server;
    private ObjectInputStream objIn;
    private ObjectOutputStream objOut;

    public ClientHandler(ClientServer client, Server server) {
        try {
            this.client = client;
            this.server = server;
            objOut = new ObjectOutputStream(this.client.getSocket().getOutputStream());
            objOut.flush();
            objIn = new ObjectInputStream(this.client.getSocket().getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (true) {
                Packet packet = (Packet) objIn.readObject();
                String username = packet.getName();

                Packet response;
                if (server.findClientByUsername(username) == null) {
                    client.setUsername(username);
                    server.addClient(client);
                    server.showClients();
                    response = new Packet("response", "1");
                } else {
                    response = new Packet("response", "0");
                }

                objOut.writeObject(response);
                objOut.flush();
            }
        } catch (Exception e) {
            System.out.println("Client " + client.getUsername() + " disconnected.");
            server.removeClient(client);
        }
    }
}
