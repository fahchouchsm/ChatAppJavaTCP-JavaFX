package com.fahchouch.server;

import java.net.Socket;

public class ClientServer {
    private String username = null;
    private Socket s;
    private static int nbrClients = 0;
    private int id;

    public ClientServer(String username, Socket s) {
        this.s = s;
        this.username = username;
        this.id = nbrClients;
        nbrClients++;
    }

    public ClientServer(Socket s) {
        this.s = s;
        this.id = nbrClients;
        nbrClients++;
    }

    public Socket getSocket() {
        return s;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
