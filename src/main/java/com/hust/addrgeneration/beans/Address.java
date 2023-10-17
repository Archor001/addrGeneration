package com.hust.addrgeneration.beans;

public class Address {
    private String username;
    private String name;
    private String nid;
    private String address;
    private int registerTime;
    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getNid() {return nid;}
    public void setNid(String nid) {this.nid = nid;}
    public String getAddress() {return address;}
    public void setAddress(String address) {this.address = address;}
    public int getRegisterTime() {return  registerTime;}
    public void setRegisterTime(int registerTime) {this.registerTime = registerTime;}
}
