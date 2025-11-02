package com.fahchouch.server;

import java.net.Socket;

public class Client {
    private String username = null;
    private Socket s;
    private static int nbrClients = 0;
    private int id;

    public Client(String username) {
        s = new Socket();
        this.username = username;
        this.id = nbrClients;
        Client.nbrClients++;
    }

    public Client() {
        s = new Socket();
        this.id = nbrClients;
        nbrClients++;
    }

    public Socket getSocket() {
        return s;
    }

}
