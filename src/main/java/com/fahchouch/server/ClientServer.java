package com.fahchouch.server;

import com.fahchouch.shared.SimpleClient;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientServer extends SimpleClient {
    private final Socket socket;
    private final ObjectOutputStream objOut;

    public ClientServer(String username, int id, Socket socket, ObjectOutputStream objOut) {
        super(username, id);
        this.socket = socket;
        this.objOut = objOut;
    }

    public ObjectOutputStream getOutputStream() {
        return objOut;
    }

    public Socket getSocket() {
        return socket;
    }
}