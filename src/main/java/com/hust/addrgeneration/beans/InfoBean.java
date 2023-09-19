package com.hust.addrgeneration.beans;

import org.springframework.stereotype.Component;

@Component
public class InfoBean {
    private String userID;
    private String password;
    private String phoneNumber;
    private String name;
    private String nid;
    private String queryAddress;
    private String userContent;
    public String getQueryAddress() {
        return queryAddress;
    }

    public void setQueryAddress(String queryAddress) {
        this.queryAddress = queryAddress;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getUserContent() {
        return userContent;
    }
    public void setUserContent(String userContent) {this.userContent = userContent;}
}
