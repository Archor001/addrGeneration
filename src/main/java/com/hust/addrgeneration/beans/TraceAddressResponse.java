package com.hust.addrgeneration.beans;

import com.hust.addrgeneration.utils.ErrorUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class TraceAddressResponse extends Response {
    private UserTrace user;
    public TraceAddressResponse(){};
    public TraceAddressResponse(int code, String msg, UserTrace user) {
        super(code, msg);
        this.user = user;
    }
    public UserTrace getUser() {return user;}
    public void setUser(UserTrace user) {this.user = user;}
}