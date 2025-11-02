package com.fahchouch.server.Room;

import java.io.Serializable;

public class Message implements Serializable {
    private String msg;
    private int idFrom;

    public Message(String msg, int idFrom) {
        this.msg = msg;
        this.idFrom = idFrom;
    }

    public String getMsg() {
        return msg;
    }

    public int getIdFrom() {
        return idFrom;
    }

}
