package com.hust.addrgeneration.service;

import com.hust.addrgeneration.beans.User;
import com.hust.addrgeneration.beans.UserManage;
import com.hust.addrgeneration.beans.UserManageResponse;
import com.hust.addrgeneration.beans.UserResponse;
import org.springframework.http.ResponseEntity;

public interface UserService {
    public ResponseEntity<UserResponse> Login(User userInfo) throws Exception;
    public ResponseEntity<UserManageResponse> FilterUsers(UserManage um) throws Exception;
}
