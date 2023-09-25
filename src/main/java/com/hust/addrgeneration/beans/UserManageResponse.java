package com.hust.addrgeneration.beans;

import com.alibaba.fastjson.JSONObject;
import com.hust.addrgeneration.utils.ErrorUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class UserManageResponse extends Response{
    private User[] users;
    private int count;
    public UserManageResponse(){};

    public UserManageResponse(int code, String msg, User[] users) {
        super(code, msg);
        this.users = users;
    }

    public User[] getUsers() { return users; }
    public void setUsers(User[] users) { this.users = users; }

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
    public ResponseEntity<UserManageResponse> responseError(int code){
        this.setCode(code);
        this.setMsg(ErrorUtils.message(code));
        return new ResponseEntity<>(this, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
