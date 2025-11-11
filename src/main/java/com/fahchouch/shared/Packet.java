package com.fahchouch.shared;

import java.io.Serializable;
import java.util.ArrayList;

public class Packet implements Serializable {
    private String name;
    private Object content;
    private ArrayList<?> arrlist;

    public Packet(String name) {
        this.name = name;
    }

    public Packet(String name, Object content) {
        this.name = name;
        this.content = content;
    }

    public Packet(String name, Object content, ArrayList<?> arrlist) {
        this.name = name;
        this.content = content;
        this.arrlist = arrlist;
    }

    public String getName() {
        return name;
    }

    public Object getContent() {
        return content;
    }

    public ArrayList<?> getArrlist() {
        return arrlist != null ? new ArrayList<>(arrlist) : null;
    }
}