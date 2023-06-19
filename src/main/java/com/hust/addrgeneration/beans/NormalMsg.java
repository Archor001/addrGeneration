package com.hust.addrgeneration.beans;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class NormalMsg {

    private int status;
    private String message;

    public NormalMsg(){};

    public NormalMsg(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
