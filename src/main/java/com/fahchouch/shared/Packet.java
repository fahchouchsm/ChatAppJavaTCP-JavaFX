package com.fahchouch.shared;

import java.io.Serializable;
import java.util.ArrayList;

public class Packet implements Serializable {
    private String name;
    private String content = null;
    private ArrayList<?> arrlist;

    public Packet(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public Packet(String name, ArrayList<?> objs) {
        this.name = name;
        this.arrlist = objs;
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

    public ArrayList<?> getArrlist() {
        return arrlist;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setArrlist(ArrayList<?> arrlist) {
        this.arrlist = arrlist;
    }
}
