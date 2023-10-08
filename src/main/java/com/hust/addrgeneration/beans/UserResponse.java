package com.hust.addrgeneration.beans;

import com.hust.addrgeneration.utils.ErrorUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class UserResponse extends Response{
    private String nid;
    public UserResponse(){};
    public UserResponse(int code, String msg, String nid) {
        super(code, msg);
        this.nid = nid;
    }

    public String getNid() { return nid;}
    public void setNid(String nid) {this.nid=nid;}

    public ResponseEntity<UserResponse> responseError(int code){
        this.setCode(code);
        this.setMsg(ErrorUtils.message(code));
        return new ResponseEntity<>(this, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
