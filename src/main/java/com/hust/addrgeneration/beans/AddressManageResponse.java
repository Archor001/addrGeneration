package com.hust.addrgeneration.beans;

public class AddressManageResponse extends Response{
    private Address[] addresses;
    private int count;
    public AddressManageResponse(){};

    public AddressManageResponse(int code, String msg, Address[] addresses) {
        super(code, msg);
        this.addresses = addresses;
    }

    public Address[] getAddresses() { return addresses; }
    public void setAddresses(Address[] addresses) { this.addresses = addresses; }
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
}
