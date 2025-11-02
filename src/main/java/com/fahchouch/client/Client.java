package com.fahchouch.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private Socket s;
    private String username = null;
    BufferedReader in;
    PrintWriter out;

    public Client() {
        try {
            s = new Socket("localhost", 3000);
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new PrintWriter(s.getOutputStream(), true);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public void sendRecString(String msg) {
        out.println(msg);
    }

    public Socket getSocket() {
        return s;
    }

    public String getUsername() {
        return username;
    }
}
