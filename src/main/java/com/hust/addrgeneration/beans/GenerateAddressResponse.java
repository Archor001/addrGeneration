package com.hust.addrgeneration.beans;

import com.hust.addrgeneration.utils.ErrorUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GenerateAddressResponse {
    private int code;
    private String msg;
    private User user;
    private String address;
    public GenerateAddressResponse(){};

    public GenerateAddressResponse(int code, String msg, User user, String address) {
        this.code = code;
        this.msg = msg;
        this.user = user;
        this.address = address;
    }

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }
    public User getUser() { return user;}
    public void setUser(User user) {this.user=user;}

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
    public ResponseEntity<GenerateAddressResponse> responseError(int code){
        this.setCode(code);
        this.setMsg(ErrorUtils.message(code));
        return new ResponseEntity<>(this, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
