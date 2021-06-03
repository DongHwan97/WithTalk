package com.sunmoon.withtalk;

public class Chatroom {
    int no;
    String name;
    int memberCount;
    String type;

    public Chatroom() {

    }

    public Chatroom(int no, String name, int memberCount, String type) {
        this.no = no;
        this.name = name;
        this.memberCount = memberCount;
        this.type = type;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
