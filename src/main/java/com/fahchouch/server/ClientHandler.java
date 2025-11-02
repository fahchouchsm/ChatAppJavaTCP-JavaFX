package com.fahchouch.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class ClientHandler extends Thread {
    private Client c;
    BufferedReader in;
    PrintWriter out;

    public ClientHandler(Client c) {
        try {
            this.c = c;
            in = new BufferedReader(new InputStreamReader(c.getSocket().getInputStream()));
            out = new PrintWriter(c.getSocket().getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {

        }
    }
}
