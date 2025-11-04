package com.fahchouch.server;

import com.fahchouch.shared.Packet;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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

            objIn = new ObjectInputStream(socket.getInputStream());

            objOut = new ObjectOutputStream(socket.getOutputStream());
            objOut.flush();

            Packet loginPacket = (Packet) objIn.readObject();
            String username = loginPacket != null ? loginPacket.getName() : null;
            System.out.println("Login attempt: " + username);

            Packet response;
            if (username == null || username.trim().isEmpty()) {
                response = new Packet("response", "0");
                objOut.writeObject(response);
                objOut.flush();
                closeSocket();
                return;
            }

            synchronized (server) {
            }
            if (server.findClientByUsername(username) == null) {

                client = new ClientServer(username, socket);
                server.addClient(client);
                server.showClients();
                response = new Packet("response", "1");
            } else {
                response = new Packet("response", "0");
            }
            objOut.writeObject(response);
            objOut.flush();
            if (!"1".equals(response.getContent())) {

                closeSocket();
                return;
            }
            while (true) {
                Packet packet = (Packet) objIn.readObject();
                if (packet == null)
                    break;
                String action = packet.getName();
                String content = packet.getContent();

                switch (action) {
                    case "searchClient":

                        Packet result = new Packet("searchResult", "ok");
                        objOut.writeObject(result);
                        objOut.flush();
                        break;

                    case "ping":
                        objOut.writeObject(new Packet("pong", "alive"));
                        objOut.flush();
                        break;

                    default:

                        objOut.writeObject(new Packet("error", "unknown action: " + action));
                        objOut.flush();
                }
            }

        } catch (Exception e) {

            String user = (client != null ? client.getUsername() : "unknown");
            System.out.println("Client " + user + " disconnected.");
        } finally {

            try {
                if (client != null && client.getUsername() != null) {
                    server.removeClient(client);
                }
                closeSocket();
            } catch (Exception ignored) {
            }
        }
    }

    private void closeSocket() {
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
}
