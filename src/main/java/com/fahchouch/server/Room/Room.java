package com.fahchouch.server.Room;

import java.util.ArrayList;

import com.fahchouch.server.Client;

public class Room {
    private int idRoom;
    private ArrayList<Client> clients = new ArrayList<>();
    private ArrayList<Message> msgs = new ArrayList<>();

    public Room() {

    }
}
