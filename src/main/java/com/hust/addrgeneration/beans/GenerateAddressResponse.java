package com.hust.addrgeneration.beans;

import com.hust.addrgeneration.utils.ErrorUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GenerateAddressResponse extends Response{
    private String address;
    public GenerateAddressResponse(){};
    public GenerateAddressResponse(int code, String msg, String address) {
        super(code, msg);
        this.address = address;
    }
    public String getAddress() { return address;}
    public void setAddress(String address) {this.address=address;}
}
