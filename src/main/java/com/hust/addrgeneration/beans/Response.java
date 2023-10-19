package com.hust.addrgeneration.beans;

import com.alibaba.fastjson.JSONObject;
import com.hust.addrgeneration.utils.ErrorUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Response {
    private int code;
    private String msg;
    public Response(){};

    public Response(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
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
    public ResponseEntity<Response> responseError(int code){
        Response resp = new Response();
        resp.setCode(code);
        resp.setMsg(ErrorUtils.message(code));
        return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}