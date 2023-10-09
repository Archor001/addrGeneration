package com.hust.addrgeneration.controller;

import com.hust.addrgeneration.beans.*;
import com.hust.addrgeneration.service.IPv6AddrService;
import com.hust.addrgeneration.service.IPv6Service;
import com.hust.addrgeneration.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AddrGeneration {
    private final IPv6AddrService iPv6AddrService;
    private final UserService userService;

    @Autowired
    public AddrGeneration(IPv6AddrService iPv6AddrService, UserService userService) {
        this.iPv6AddrService = iPv6AddrService;
        this.userService = userService;
    }

    // 登录
    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody User userInfo) throws Exception {return userService.Login(userInfo);}

    // 批量获取用户
    @GetMapping(value="/user")
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

    // 创建用户
    @PutMapping(value = "/user")
    public ResponseEntity<UserResponse> register(@RequestBody User userInfo) throws Exception {return userService.createUser(userInfo);}

    // 删除用户(支持批量)
    @DeleteMapping(value = "/user")
    public ResponseEntity<Response> deleteUser(@RequestParam("userContent") String userContent) throws Exception {return userService.deleteUser(userContent);}

    // 修改用户
    @PostMapping(value = "/user")
    public ResponseEntity<UserResponse> updateUser(@RequestBody User userInfo) throws Exception {return userService.updateUser(userInfo);}

    // 地址生成
    @PostMapping(value = "/address")
    public ResponseEntity<GenerateAddressResponse> generateAddress(@RequestBody GenerateAddress generateAddress) throws Exception { return iPv6AddrService.generateAddress(generateAddress);}

    // 地址查询
    @GetMapping(value = "/query")
    public ResponseEntity<QueryAddressResponse> queryAddress(@RequestParam("nid") String nid) throws Exception {return iPv6AddrService.queryAddress(nid);}

    // 地址溯源
    @GetMapping(value = "/address")
    public ResponseEntity<TraceAddressResponse> traceAddress(@RequestParam("queryAddress") String queryAddress) throws Exception {return iPv6AddrService.traceAddress(queryAddress);}

    // 地址删除
    @DeleteMapping(value = "/address")
    public ResponseEntity<Response> deleteAddress(@RequestParam("deleteAddress") String address) throws Exception {return iPv6AddrService.deleteAddress(address);}
}
