package com.hust.addrgeneration.beans;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class NormalMsg {

    private int code;
    private String msg;
    private JSONObject info;
    public NormalMsg(){};

    public NormalMsg(int code, String msg, JSONObject info) {
        this.code = code;
        this.msg = msg;
        this.info = info;
    }

    public JSONObject getInfo() { return info; }

    public void setInfo(JSONObject info) {
        this.info = info;
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

}
