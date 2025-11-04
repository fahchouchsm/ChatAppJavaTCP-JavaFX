package com.fahchouch.shared;

import java.io.Serializable;

public class SimpleClient implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private int id;

    public SimpleClient(String username, int id) {
        this.username = username;
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getId() {
        return id;
    }
}
