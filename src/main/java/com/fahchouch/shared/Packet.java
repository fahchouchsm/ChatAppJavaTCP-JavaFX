package com.fahchouch.shared;

import java.io.Serializable;

public class Packet implements Serializable {
    private String name;
    private String content;

    public Packet(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public Packet(String name) {
        this(name, null);
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }
}
