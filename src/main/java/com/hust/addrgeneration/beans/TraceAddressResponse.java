package com.hust.addrgeneration.beans;

import com.hust.addrgeneration.utils.ErrorUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class TraceAddressResponse extends Response {
    private User user;
    public TraceAddressResponse(){};
    public TraceAddressResponse(int code, String msg, int status, User user) {
        super(code, msg);
        this.user = user;
    }
    public User getUser() {return user;}
    public void setUser(User user) {this.user = user;}
    public ResponseEntity<TraceAddressResponse> responseError(int code){
        this.setCode(code);
        this.setMsg(ErrorUtils.message(code));
        return new ResponseEntity<>(this, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}