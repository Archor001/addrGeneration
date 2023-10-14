package com.hust.addrgeneration.service;

import com.hust.addrgeneration.beans.*;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<UserResponse> Login(User userInfo) throws Exception;
    ResponseEntity<UserManageResponse> FilterUsers(UserManage um) throws Exception;
    ResponseEntity<Response> DeleteUser(String nid) throws Exception;
}
