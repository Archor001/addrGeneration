package com.hust.addrgeneration.beans;

import com.alibaba.fastjson.JSONObject;
import com.hust.addrgeneration.utils.ErrorUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class QueryAddressResponse extends Response{
    private String address;
    private User user;
    public QueryAddressResponse(){};
    public QueryAddressResponse(int code, String msg, String address) {
        super(code, msg);
        this.address = address;
    }
    public void setAddress(String address) {this.address=address;}
    public String getAddress() {return address;}
    public void setUser(User user) {this.user = user;}
    public User getUser() {return user;}
}
