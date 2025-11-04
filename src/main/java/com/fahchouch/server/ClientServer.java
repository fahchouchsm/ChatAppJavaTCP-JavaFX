package com.fahchouch.server;

import java.net.Socket;
import com.fahchouch.shared.SimpleClient;

public class ClientServer extends SimpleClient {
    private Socket socket;
    private static int nbrClients = 0;

    public ClientServer(Socket socket) {
        super(null, nbrClients);
        this.socket = socket;
        nbrClients++;
    }

    public ClientServer(String username, Socket socket) {
        super(username, nbrClients);
        this.socket = socket;
        nbrClients++;
    }

    public Socket getSocket() {
        return socket;
    }
}
