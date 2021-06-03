package com.sunmoon.withtalk;

public class Friend {
    public String id;
    public String name;

    public Friend() {

    }

    public Friend(String friendId, String name) {
        this.id = friendId;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
