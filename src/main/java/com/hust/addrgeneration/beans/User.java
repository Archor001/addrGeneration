package com.hust.addrgeneration.beans;

import org.springframework.stereotype.Component;

@Component
public class User {
    private String userID;
    private String password;
    private String phoneNumber;
    private String username;
    private String nid;
    private int status;

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) { this.nid = nid; }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
