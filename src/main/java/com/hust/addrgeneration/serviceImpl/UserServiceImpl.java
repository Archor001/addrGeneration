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

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


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
    public ResponseEntity<?> Login(User userInfo) {
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
    public ResponseEntity<?> FilterUsers(int offset, int limit, String content) {
        UserManageResponse response = new UserManageResponse();

        List<UserAddress> userList;
        try{
            userList = userMapper.getUsersByFilter(offset, limit, content);
        } catch (Exception e) {
            return response.responseError(10017);
        }
        UserAddress[] users = userList.toArray(new UserAddress[userList.size()]);
        int userCount = 0;
        try{
            userCount = userMapper.getUserCountByFilter(content);
        } catch (Exception e){
            return response.responseError(10017);
        }

        List<UserAddress> rntUsersList = new ArrayList<>();
        // 获取用户生成的地址
        for(UserAddress user: users){
            List<Address> address = userMapper.getAddress(user.getNid());
            if(address.size() <= 0) {
                rntUsersList.add(user);
                continue;
            }

            String[] addressArray = address.stream().map((Address::getAddress)).toArray(String[]::new);
            String[] registerTimeArray = address.stream().map(a -> String.valueOf(a.getRegisterTime())).toArray(String[]::new);
            String str1 = String.join(",", addressArray);
            String str2 = String.join(",", registerTimeArray);
            user.setAddress(str1);
            user.setRegisterTime(str2);
            rntUsersList.add(user);
        }

        response.setCode(0);
        response.setMsg("success");
        response.setUsers(rntUsersList.toArray(new UserAddress[rntUsersList.size()]));
        response.setCount(userCount);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 创建用户
    @Override
    public ResponseEntity<?> createUser(User infoBean) throws Exception {
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
        if( role<1 || role>5 ){
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
            userMapper.register(nid, password, username, phoneNumber, name, emailAddress, role, 1);
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
    public ResponseEntity<?> deleteUser(String userContent) throws  Exception {
        Response response = new Response();
        if(userContent.contains(",")){  // 批量
            String[] users = userContent.split(",");
            for(String user : users){
                try {
                    userMapper.deleteUser(user);
                } catch (Exception e){
                    return response.responseError(10004);
                }
            }
        } else {
            try {
                userMapper.deleteUser(userContent);
            } catch (Exception e){
                return response.responseError(10004);
            }
        }
        response.setMsg("success");
        response.setCode(0);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 修改用户
    @Override
    public ResponseEntity<?> updateUser(User infoBean) throws  Exception {
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