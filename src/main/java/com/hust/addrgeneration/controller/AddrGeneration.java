package com.hust.addrgeneration.controller;

import com.alibaba.fastjson.JSONObject;
import com.hust.addrgeneration.beans.*;
import com.hust.addrgeneration.service.IPv6AddrService;
import com.hust.addrgeneration.service.IPv6Service;
import com.hust.addrgeneration.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AddrGeneration {
    private final IPv6Service ipv6Service;
    private final IPv6AddrService iPv6AddrService;
    private final UserService userService;

    @Autowired
    public AddrGeneration(@Qualifier("IPv6ServiceImpl") IPv6Service iPv6Service, IPv6AddrService iPv6AddrService, UserService userService) {
        this.ipv6Service = iPv6Service;
        this.iPv6AddrService = iPv6AddrService;
        this.userService = userService;
    }

    // 登录
    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody User userInfo) throws Exception {
        return userService.Login(userInfo);
    }

    // 用户注册(申请NID)
    @PutMapping(value = "/user/nid")
    public ResponseEntity<UserResponse> register(@RequestBody User userInfo) throws Exception {
        return iPv6AddrService.registerNID(userInfo);
    }

    // 批量获取用户
    @GetMapping(value="/admin/user")
    public ResponseEntity<UserManageResponse> manageUser(
            @RequestParam("offset") int offset,
            @RequestParam("limit") int limit,
            @RequestParam("content") String content) throws Exception {
        UserManage um = new UserManage();
        um.setOffset(offset);
        um.setLimit(limit);
        um.setContent(content);
        return userService.FilterUsers(um);
    }

    @DeleteMapping(value="/admin/user")
    public ResponseEntity<UserResponse> deleteUser(@RequestParam("nid") String nid) throws Exception{
        return userService.DeleteUser(nid);
    }

    // 地址生成
    @PostMapping(value = "/user/address")
    public ResponseEntity<AddressResponse> createAddress(@RequestBody Address addressInfo) throws Exception {
        return iPv6AddrService.createAddr(addressInfo);
    }

    // 地址查询
    @GetMapping(value = "/user/address")
    public ResponseEntity<QueryResponse> queryAddr(@RequestParam("queryAddress") String queryAddress, @RequestParam("prefixLength") int prefixLength) throws Exception {
        Query queryInfo = new Query();
        queryInfo.setQueryAddress(queryAddress);
        queryInfo.setPrefixLength(prefixLength);
        return iPv6AddrService.queryAddr(queryInfo);
    }
}
