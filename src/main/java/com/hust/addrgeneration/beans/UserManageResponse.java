package com.hust.addrgeneration.beans;

import com.alibaba.fastjson.JSONObject;
import com.hust.addrgeneration.utils.ErrorUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class UserManageResponse extends Response{
    private UserAddress[] users;
    private int count;
    public UserManageResponse(){};

    public UserManageResponse(int code, String msg, UserAddress[] users) {
        super(code, msg);
        this.users = users;
    }

    public UserAddress[] getUsers() { return users; }
    public void setUsers(UserAddress[] users) { this.users = users; }
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
}