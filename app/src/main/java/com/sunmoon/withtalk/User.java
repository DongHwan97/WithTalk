package com.sunmoon.withtalk;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String name;
    private String pw;
    private String phoneNo;

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

    public String getpw() {
        return pw;
    }

    public void setpw(String pw) {
        this.pw = pw;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }
}
