package com.hust.addrgeneration.serviceImpl;

import com.alibaba.fastjson.JSONObject;
import com.hust.addrgeneration.beans.*;
import com.hust.addrgeneration.dao.UserMapper;
import com.hust.addrgeneration.service.UserService;
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
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public ResponseEntity<UserResponse> Login(User userInfo) {
        UserResponse response = new UserResponse();
        String username = userInfo.getUsername();
        String password = userInfo.getPassword();
        if( !username.equals(custom.getAdmin()) || !password.equals(custom.getPassword()) ){
            response.responseError(10005);
        }
        response.setCode(0);
        response.setMsg("success");
        return new ResponseEntity<UserResponse>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserManageResponse> FilterUsers(UserManage um) {
        UserManageResponse response = new UserManageResponse();

        int offset = um.getOffset();
        int limit = um.getLimit();
        String content = um.getContent();
        List<User> userList = new ArrayList<User>();

        try{
            userList = userMapper.getUsersByFilter(offset, limit, content);
        } catch (Exception e) {
            response.responseError(10010);
        }
        User[] users = userList.toArray(new User[userList.size()]);
        for(User i : users){
            String address = i.getAddress();
            if(address == null || address.isEmpty())
                continue;
            StringBuilder sb = new StringBuilder();
            for(int len=0;len<address.length();len++){
                if(len > 0 && len % 4 ==0)
                    sb.append(":");
                sb.append(address.charAt(len));
            }
            i.setAddress(sb.toString());
        }
        int userCount = 0;
        try{
            userCount = userMapper.getUserCountByFilter(content);
        } catch (Exception e){
            response.responseError(10010);
        }
        response.setCode(0);
        response.setMsg("success");
        response.setUsers(users);
        response.setCount(userCount);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserResponse> DeleteUser(String nid){
        UserResponse response = new UserResponse();
        User user = userMapper.queryRegisterInfo(nid);
        if(user == null){
            response.responseError(10014);
        }
        try{
            userMapper.deleteUser(nid);
        } catch(Exception e) {
            response.responseError(10013);
        }
        response.setCode(0);
        response.setMsg("success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
