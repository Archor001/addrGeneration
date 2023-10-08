package com.hust.addrgeneration.beans;

import com.alibaba.fastjson.JSONObject;
import com.hust.addrgeneration.utils.ErrorUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class QueryAddressResponse extends Response{
    private String address;
    public QueryAddressResponse(){};
    public QueryAddressResponse(int code, String msg, String address) {
        super(code, msg);
        this.address = address;
    }
    public void setAddress(String address) {this.address=address;}
    public String getAddress() {return address;}
    public ResponseEntity<QueryAddressResponse> responseError(int code){
        this.setCode(code);
        this.setMsg(ErrorUtils.message(code));
        return new ResponseEntity<>(this, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
