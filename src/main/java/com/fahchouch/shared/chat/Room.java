package com.fahchouch.shared.chat;

import com.fahchouch.server.ClientServer;
import com.fahchouch.shared.Packet;

import java.util.ArrayList;

public class Room {
  private static int nextRoomId = 1000;
  private final int roomId;
  private final ArrayList<ClientServer> participants = new ArrayList<>();
  private final ArrayList<String[]> messageHistory = new ArrayList<>(); // [sender, msg]

  public Room() {
    this.roomId = getNextRoomId();
  }

  private static synchronized int getNextRoomId() {
    return nextRoomId++;
  }

  public void addParticipant(ClientServer client) {
    if (client != null && !participants.contains(client)) {
      participants.add(client);
    }
  }

  public ArrayList<ClientServer> getParticipants() {
    return new ArrayList<>(participants);
  }

  public String getName() {
    return String.valueOf(roomId);
  }

  public void broadcastMessage(String sender, String message) {
    // Save to history
    messageHistory.add(new String[] { sender, message });

    ArrayList<String> data = new ArrayList<>();
    data.add(getName());
    data.add(sender);
    data.add(message);

    for (ClientServer p : participants) {
      try {
        p.getOutputStream().writeObject(new Packet("message", null, data));
        p.getOutputStream().flush();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public ArrayList<String[]> getMessageHistory() {
    return new ArrayList<>(messageHistory);
  }

  public void broadcastFile(String sender, String payload) {
    messageHistory.add(new String[] { sender, payload, "file" });

    ArrayList<String> data = new ArrayList<>();
    data.add(getName());
    data.add(sender);
    data.add(payload);

    for (ClientServer p : participants) {
      try {
        p.getOutputStream().writeObject(new Packet("file", null, data));
        p.getOutputStream().flush();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}