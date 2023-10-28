package com.hust.addrgeneration.service;

import com.hust.addrgeneration.beans.*;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<?> Login(User userInfo) throws Exception;
    ResponseEntity<?> FilterUsers(int offset, int limit, String content) throws Exception;
    ResponseEntity<?> DeleteUser(String nid) throws Exception;
}
