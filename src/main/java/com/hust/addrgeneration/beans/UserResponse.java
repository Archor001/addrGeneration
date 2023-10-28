package com.hust.addrgeneration.beans;

import com.hust.addrgeneration.utils.ErrorUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class UserResponse extends Response{
    private User user;
    public UserResponse(){};

    public UserResponse(int code, String msg, User user) {
        super(code, msg);
        this.user = user;
    }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
