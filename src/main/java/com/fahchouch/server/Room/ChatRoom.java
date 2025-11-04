package com.fahchouch.server.Room;

import com.fahchouch.server.ClientServer;
import java.util.ArrayList;
import java.util.List;

public class ChatRoom {
    private static int nextRoomId = 1;
    private final int roomId;
    private final String name;
    private final List<ClientServer> participants = new ArrayList<>();

    public ChatRoom(String name) {
        this.roomId = getNextRoomId();
        this.name = name;
    }

    private static synchronized int getNextRoomId() {
        return nextRoomId++;
    }

    public void addParticipant(ClientServer client) {
        if (!participants.contains(client)) {
            participants.add(client);
        }
    }

    public List<ClientServer> getParticipants() {
        return participants;
    }

    public String getName() {
        return name;
    }

    public int getRoomId() {
        return roomId;
    }
}
