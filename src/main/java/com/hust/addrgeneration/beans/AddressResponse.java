package com.hust.addrgeneration.beans;

public class AddressResponse {
    private int code;
    private String msg;
    private String address;
    public AddressResponse(){};

    public AddressResponse(int code, String msg, String address) {
        this.code = code;
        this.msg = msg;
        this.address = address;
    }

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
