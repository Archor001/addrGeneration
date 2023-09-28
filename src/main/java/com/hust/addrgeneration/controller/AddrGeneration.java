package com.hust.addrgeneration.controller;

import com.alibaba.fastjson.JSONObject;
import com.hust.addrgeneration.beans.*;
import com.hust.addrgeneration.service.IPv6AddrService;
import com.hust.addrgeneration.service.IPv6Service;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
public class AddrGeneration {
    private final IPv6Service ipv6Service;
    private final IPv6AddrService iPv6AddrService;

    @Autowired
    public AddrGeneration(@Qualifier("IPv6ServiceImpl") IPv6Service iPv6Service, IPv6AddrService iPv6AddrService) {
        this.ipv6Service = iPv6Service;

        this.iPv6AddrService = iPv6AddrService;
    }

    // 创建用户
    @PutMapping(value = "/user")
    public ResponseEntity<RegisterResponse> register(@RequestBody InfoBean userInfo) throws Exception {return iPv6AddrService.createUser(userInfo);}

    // 删除用户
    @DeleteMapping(value = "/user")
    public ResponseEntity<Response> deleteUser(@RequestBody InfoBean userInfo) throws Exception {return iPv6AddrService.deleteUser(userInfo);}

    // 修改用户
    @PostMapping(value = "/user")
    public ResponseEntity<Response> updateUser(@RequestBody InfoBean userInfo) throws Exception {return iPv6AddrService.updateUser(userInfo);}

    // 地址生成
    @PostMapping(value = "/address")
    public ResponseEntity<GenerateAddressResponse> generateAddress(@RequestBody InfoBean userInfo) throws Exception { return iPv6AddrService.generateAddr(userInfo);}

    // 地址查询（溯源）
    @GetMapping(value = "/address")
    public ResponseEntity<QueryAddressResponse> queryAddr(@RequestBody InfoBean userInfo) throws Exception {return iPv6AddrService.queryAddr(userInfo);}

    // 地址删除
}
