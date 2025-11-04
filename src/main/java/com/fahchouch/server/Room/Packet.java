package com.fahchouch.server.Room;

import java.io.Serializable;

public class Packet implements Serializable {
    private String name;
    private String content;

    public Packet(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public Packet(String name) {
        this.name = name;
        this.content = null;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }
}
