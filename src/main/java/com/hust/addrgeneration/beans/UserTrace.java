package com.hust.addrgeneration.beans;

public class UserTrace extends User{
    private long registerTime;
    private int addressStatus;

    public long getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(long registerTime) {
        this.registerTime = registerTime;
    }
    public int getAddressStatus() {return addressStatus;}
    public void setAddressStatus(int addressStatus) {this.addressStatus = addressStatus;}
}
