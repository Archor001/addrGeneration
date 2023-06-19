package com.hust.addrgeneration.beans;

import com.alibaba.fastjson.JSONObject;

public class RegisterInfo extends NormalMsg{
    private JSONObject info;

    public JSONObject getInfo() {
        return info;
    }

    public void setInfo(JSONObject info) {
        this.info = info;
    }
}
