package com.hust.addrgeneration.serviceImpl;

import com.alibaba.fastjson.JSONObject;
import com.hust.addrgeneration.beans.*;
import com.hust.addrgeneration.dao.UserMapper;
import com.hust.addrgeneration.service.UserService;
import com.hust.addrgeneration.utils.AddressUtils;
import com.hust.addrgeneration.utils.ISPUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;

    @Autowired
    private Custom custom;
    private ISP ispPrefix = ISPUtils.ispPrefix;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public ResponseEntity<?> Login(User userInfo) {
        UserResponse response = new UserResponse();
        String username = userInfo.getUsername();
        String password = userInfo.getPassword();
        if( !username.equals(custom.getAdmin()) || !password.equals(custom.getPassword()) ){
            return response.responseError(10005);
        }
        response.setCode(0);
        response.setMsg("success");
        return new ResponseEntity<UserResponse>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> FilterUsers(int offset, int limit, String content) {
        UserManageResponse response = new UserManageResponse();

        if(ispPrefix.getIsp() == null || ispPrefix.getIsp().length() == 0){
            response.setCode(0);
            response.setMsg("success");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        List<User> userList = new ArrayList<User>();

        try{
            userList = userMapper.getUsersByFilter(offset, limit, content);
        } catch (Exception e) {
            return response.responseError(10010);
        }
        User[] users = userList.toArray(new User[userList.size()]);
        int userCount = 0;
        try{
            userCount = userMapper.getUserCountByFilter(content);
        } catch (Exception e){
            return response.responseError(10010);
        }
        response.setCode(0);
        response.setMsg("success");
        response.setUsers(users);
        response.setCount(userCount);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> DeleteUser(String phoneNumber){
        Response response = new Response();
        User user = userMapper.queryPhoneNumber(phoneNumber);
        if(user == null){
            return response.responseError(10014);
        }
        try{
            userMapper.deleteUser(phoneNumber);
        } catch(Exception e) {
            return response.responseError(10013);
        }
        response.setCode(0);
        response.setMsg("success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
