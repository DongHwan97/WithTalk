package com.sunmoon.withtalk;

public class Friend {
    public String name;
    public String id;

    public Friend() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Friend(String friendId, String name) {
        this.id = friendId;
        this.name = name;
    }
}
