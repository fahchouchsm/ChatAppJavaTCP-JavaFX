package com.fahchouch.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class ClientHandler extends Thread {
    private ClientServer c;
    private BufferedReader in;
    private PrintWriter out;
    private Server server;

    public ClientHandler(ClientServer c, Server server) {
        try {
            this.c = c;
            this.server = server;
            in = new BufferedReader(new InputStreamReader(c.getSocket().getInputStream()));
            out = new PrintWriter(c.getSocket().getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                System.out.println("waiting for the client username");
                String usernamereq = in.readLine();
                if (server.findClientByUsername(usernamereq) == null) {
                    c.setUsername(usernamereq);
                    server.addClient(c);
                    out.println(1);
                    out.flush();
                    server.showClients();
                } else {
                    out.print(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
