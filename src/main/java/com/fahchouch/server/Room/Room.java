package com.fahchouch.server.Room;

import java.util.ArrayList;

import com.fahchouch.server.ClientServer;

public class Room {
    private int idRoom;
    private ArrayList<ClientServer> clients = new ArrayList<>();
    private ArrayList<Message> msgs = new ArrayList<>();

    public Room() {

    }
}
