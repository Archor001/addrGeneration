package com.hust.addrgeneration.beans;

import org.springframework.stereotype.Component;

@Component
public class User {
    private String username;
    private String password;
    private String phoneNumber;
    private String name;
    private String nid;
    private String emailAddress;
    private int role;
    private String address;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

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

    public String getNid() {
        return nid;
    }
    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getEmailAddress() {
        return emailAddress;
    }
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public int getRole() {
        return role;
    }
    public void setRole(int role) {this.role = role;}
    public String getAddress() {return address;}
    public void setAddress(String address) {this.address = address;}
}
