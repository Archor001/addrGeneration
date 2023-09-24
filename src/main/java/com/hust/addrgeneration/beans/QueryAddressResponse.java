package com.hust.addrgeneration.beans;

import com.alibaba.fastjson.JSONObject;
import com.hust.addrgeneration.utils.ErrorUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class QueryAddressResponse extends Response{
    private int code;
    private String msg;
    private JSONObject info;
    public QueryAddressResponse(){};

    public QueryAddressResponse(int code, String msg, JSONObject info) {
        this.code = code;
        this.msg = msg;
        this.info = info;
    }

    public JSONObject getInfo() { return info; }

    public void setInfo(JSONObject info) { this.info = info; }

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
    public ResponseEntity<QueryAddressResponse> responseError(int code){
        this.setCode(code);
        this.setMsg(ErrorUtils.message(code));
        return new ResponseEntity<>(this, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
