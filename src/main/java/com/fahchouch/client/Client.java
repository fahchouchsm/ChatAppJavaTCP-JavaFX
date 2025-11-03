package com.fahchouch.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.fahchouch.client.fx.FxEventHandler;

public class Client {
    private Socket s;
    private String username = null;
    BufferedReader in;
    PrintWriter out;

    public Client() {
        try {
            s = new Socket("localhost", 3001);
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new PrintWriter(s.getOutputStream(), true);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public String sendRecString(String msg) {
        try {
            out.println(msg);
            out.flush();
            String res = in.readLine();
            return res;
        } catch (IOException e) {
            FxEventHandler.showAlert("Impossible de se connecter au serveur");
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Socket getSocket() {
        return s;
    }

    public String getUsername() {
        return username;
    }

    public static boolean isUsernameValid(String username) {
        if (username == null || username.isEmpty() || username.length() < 3)
            return false;
        return !Character.isDigit(username.charAt(0));
    }

}
