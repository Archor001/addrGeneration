package com.hust.addrgeneration.serviceImpl;

import com.hust.addrgeneration.beans.*;
import com.hust.addrgeneration.dao.UserMapper;
import com.hust.addrgeneration.service.UserService;
import com.hust.addrgeneration.utils.AddressUtils;
import com.hust.addrgeneration.utils.ConvertUtils;
import com.hust.addrgeneration.utils.HashUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


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
            return response.responseError(10002);
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
        List<User> userList;

        try{
            userList = userMapper.getUsersByFilter(offset, limit, content);
        } catch (Exception e) {
            return response.responseError(10017);
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
            String displayAddr = AddressUtils.displayAddress(sb.toString());
            i.setAddress(displayAddr);
        }
        int userCount = 0;
        try{
            userCount = userMapper.getUserCountByFilter(content);
        } catch (Exception e){
            return response.responseError(10017);
        }
        response.setCode(0);
        response.setMsg("success");
        response.setUsers(users);
        response.setCount(userCount);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 创建用户
    @Override
    public ResponseEntity<UserResponse> createUser(User infoBean) throws Exception {
        UserResponse response = new UserResponse();
        String name = infoBean.getName();
        String password = infoBean.getPassword();
        String phoneNumber = infoBean.getPhoneNumber();
        String username = infoBean.getUsername();
        String emailAddress = infoBean.getEmailAddress();
        int role = infoBean.getRole();

        // Step1. 检查参数合法性
        String phoneRegexp = "^((13[0-9])|(14[57])|(15[0-35-9])|(16[2567])|(17[0-8])|(18[0-9])|(19[0-9]))\\d{8}$";
        if(!Pattern.matches(phoneRegexp,phoneNumber)){
            return response.responseError(10005);
        }
        if( role<0 || role>4 ){
            return response.responseError(10006);
        }
        String emailRegexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";
        if(!Pattern.matches(emailRegexp,emailAddress)){
            return response.responseError(10007);
        }

        // Step2. 生成NID
        String nid = generateNID(username, phoneNumber, name);

        // Step3. 写入数据库
        try {
            userMapper.register(nid, password, username, phoneNumber, name, emailAddress, role);
        } catch (Exception e) {
            return response.responseError(10003);
        }
        response.setNid(nid);
        response.setCode(0);
        response.setMsg("success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 删除用户
    @Override
    public ResponseEntity<Response> deleteUser(String userContent) throws  Exception {
        Response response = new Response();
        if(userContent.contains(",")){  // 批量
            String[] users = userContent.split(",");
            for(String user : users){
                try {
                    userMapper.deleteUser(user);
                } catch (Exception e){
                    return response.responseNormalError(10004);
                }
            }
        } else {
            try {
                userMapper.deleteUser(userContent);
            } catch (Exception e){
                return response.responseNormalError(10004);
            }
        }
        response.setMsg("success");
        response.setCode(0);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 修改用户
    @Override
    public ResponseEntity<UserResponse> updateUser(User infoBean) throws  Exception {
        UserResponse response = new UserResponse();

        // Step1. 检查nid是否存在
        String nid = infoBean.getNid();
        if(nid == null || nid.length() <= 0){
            return response.responseError(10009);
        }
        User user;
        try{
            user = userMapper.getUser(nid);
        } catch (Exception e){
            return response.responseError(10008);
        }

        // Step2. 检查参数合法性
        String username = user.getUsername();
        String password = infoBean.getPassword();
        String name = infoBean.getName();
        String phoneNumber = infoBean.getPhoneNumber();
        String emailAddress = infoBean.getEmailAddress();
        int role = infoBean.getRole();

        String phoneRegexp = "^((13[0-9])|(14[57])|(15[0-35-9])|(16[2567])|(17[0-8])|(18[0-9])|(19[0-9]))\\d{8}$";
        if(!Pattern.matches(phoneRegexp,phoneNumber)){
            return response.responseError(10005);
        }
        if( role<0 || role>4 ){
            return response.responseError(10006);
        }
        String emailRegexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";
        if(!Pattern.matches(emailRegexp,emailAddress)){
            return response.responseError(10007);
        }

        // Step3. 重新生成NID
        String newNID = generateNID(username,phoneNumber,name);
        if(password != null) user.setPassword(password);
        if(name != null) user.setName(name);
        if(phoneNumber != null) user.setPhoneNumber(phoneNumber);
        if(emailAddress != null) user.setEmailAddress(emailAddress);
        if(role>=1&&role<=5) user.setRole(role);
        user.setNid(newNID);

        // Step4. 数据库更新
        try{
            userMapper.updateUser(user);
        } catch (Exception e){
            return response.responseError(10008);
        }
        response.setCode(0);
        response.setMsg("success");
        response.setNid(newNID);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private String generateNID(String username, String phoneNumber, String name){
        String encryptStr = username + phoneNumber + name;
        String hashStr = HashUtils.SM3Hash(encryptStr);
        String userPart = ConvertUtils.hexStringToBinString(hashStr).substring(0,38);
        String userType = username.substring(0,1);
        String organizationPart = "";
        switch (userType) {
            case "U" :
                organizationPart = "00";
                break;
            case "M" :
                organizationPart = "01";
                break;
            case "D" :
                organizationPart = "10";
                break;
            default:
                organizationPart = "11";
                break;
        }
        return ConvertUtils.binStringToHexString(userPart + organizationPart);
    }
}