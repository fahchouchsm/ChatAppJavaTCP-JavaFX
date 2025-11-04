package com.fahchouch.server;

import com.fahchouch.shared.SimpleClient;
import java.net.Socket;

public class ClientServer extends SimpleClient {
    private Socket socket;

    public ClientServer(String username, int id, Socket socket) {
        super(username, id);
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }
}
