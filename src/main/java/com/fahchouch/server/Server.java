package com.fahchouch.server;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private int port;

    public static void main(String[] args) {
        new Server(3000).runServer();
    }

    public Server(int port) {
        this.port = port;
    }

    public void runServer() {
        try {
            ServerSocket ss = new ServerSocket(3001);
            System.out.println("server running on port : " + ss.getLocalPort());
            while (true) {
                Socket s = ss.accept();
                Client c = new Client();
                new ClientHandler(c).run();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
