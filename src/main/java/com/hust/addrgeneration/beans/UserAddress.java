package com.hust.addrgeneration.beans;

public class UserAddress extends User{
    private String address;
    private String registerTime;
    public String getAddress() {return address;}
    public void setAddress(String address) {this.address = address;}
    public String getRegisterTime() {return registerTime;}
    public void setRegisterTime(String registerTime) {this.registerTime = registerTime;}
}
