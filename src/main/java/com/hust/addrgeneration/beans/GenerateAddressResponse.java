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

    public String getAdress() { return address;}
    public void setAddress(String address) {this.address=address;}

    public ResponseEntity<GenerateAddressResponse> responseError(int code){
        this.setCode(code);
        this.setMsg(ErrorUtils.message(code));
        return new ResponseEntity<>(this, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
