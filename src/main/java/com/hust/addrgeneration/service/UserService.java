package com.hust.addrgeneration.service;

import com.hust.addrgeneration.beans.*;
import org.springframework.http.ResponseEntity;

public interface UserService {
    public ResponseEntity<?> Login(User userInfo) throws Exception;
    public ResponseEntity<?> FilterUsers(int offset, int limit, String content) throws Exception;
    ResponseEntity<?> createUser(User infoBean) throws Exception;
    ResponseEntity<?> deleteUser(String userContent) throws Exception;
    ResponseEntity<?> updateUser(User infoBean) throws Exception;
}