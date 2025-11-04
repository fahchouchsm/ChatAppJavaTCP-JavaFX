package com.fahchouch.shared;

import java.io.Serializable;

public class Packet implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name; // action / username
    private String content; // payload or response code

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

    public void setContent(String content) {
        this.content = content;
    }
}
