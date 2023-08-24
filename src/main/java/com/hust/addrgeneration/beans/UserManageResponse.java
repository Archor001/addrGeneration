package com.hust.addrgeneration.beans;

import com.alibaba.fastjson.JSONObject;

public class UserManageResponse {
    private int code;
    private String msg;
    private User[] users;
    private int count;
    public UserManageResponse(){};

    public UserManageResponse(int code, String msg, User[] users) {
        this.code = code;
        this.msg = msg;
        this.users = users;
    }

    public User[] getUsers() { return users; }

    public void setUsers(User[] users) { this.users = users; }

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
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
}
