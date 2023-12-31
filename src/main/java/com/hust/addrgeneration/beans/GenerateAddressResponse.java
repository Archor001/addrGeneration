package com.hust.addrgeneration.beans;

import com.hust.addrgeneration.utils.ErrorUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GenerateAddressResponse extends Response{
    private UserAddress user;
    public GenerateAddressResponse(){};
    public GenerateAddressResponse(int code, String msg, UserAddress user) {
        super(code, msg);
        this.user = user;
    }
    public UserAddress getUser() { return user;}
    public void setUser(UserAddress user) {this.user=user;}
}
