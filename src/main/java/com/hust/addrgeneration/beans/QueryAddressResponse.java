package com.hust.addrgeneration.beans;

import com.alibaba.fastjson.JSONObject;
import com.hust.addrgeneration.utils.ErrorUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class QueryAddressResponse extends Response{
    private JSONObject info;
    public QueryAddressResponse(){};

    public QueryAddressResponse(int code, String msg, JSONObject info) {
        super(code, msg);
        this.info = info;
    }

    public JSONObject getInfo() { return info; }

    public void setInfo(JSONObject info) { this.info = info; }

    public ResponseEntity<QueryAddressResponse> responseError(int code){
        this.setCode(code);
        this.setMsg(ErrorUtils.message(code));
        return new ResponseEntity<>(this, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
