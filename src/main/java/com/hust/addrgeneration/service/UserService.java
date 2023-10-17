package com.hust.addrgeneration.service;

import com.hust.addrgeneration.beans.*;
import org.springframework.http.ResponseEntity;

public interface UserService {
    public ResponseEntity<UserResponse> Login(User userInfo) throws Exception;
    public ResponseEntity<UserManageResponse> FilterUsers(int offset, int limit, String content) throws Exception;
    ResponseEntity<UserResponse> createUser(User infoBean) throws Exception;
    ResponseEntity<Response> deleteUser(String userContent) throws Exception;
    ResponseEntity<UserResponse> updateUser(User infoBean) throws Exception;
}