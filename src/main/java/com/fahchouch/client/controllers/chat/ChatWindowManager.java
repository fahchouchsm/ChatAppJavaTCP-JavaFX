package com.fahchouch.client.controllers.chat;

import com.fahchouch.shared.Packet;
import javafx.application.Platform;
import java.util.ArrayList;

public class ChatWindowManager {
  private static final ArrayList<ChatPageController> openChats = new ArrayList<>();

  public static void register(ChatPageController controller) {
    if (controller != null && !openChats.contains(controller)) {
      openChats.add(controller);
    }
  }

  public static void handleIncomingMessage(Packet packet) {
    ArrayList<?> data = packet.getArrlist();
    if (data == null || data.size() < 3)
      return;

    String room = (String) data.get(0);
    String sender = (String) data.get(1);
    String msg = (String) data.get(2);

    Platform.runLater(() -> {
      for (ChatPageController chat : new ArrayList<>(openChats)) {
        if (room.equals(chat.getRoomName())) {
          boolean isMe = sender.equals(chat.getClient().getUsername());

          if (!isMe) {
            chat.appendMessage(sender, msg, false);
          }
        }
      }
    });
  }

  public static void handleFile(Packet packet) {
    Platform.runLater(() -> {
      for (ChatPageController chat : new ArrayList<>(openChats)) {
        chat.handleFileReceived(packet);
      }
    });
  }
}