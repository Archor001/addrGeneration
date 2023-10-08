package com.hust.addrgeneration.controller;

import com.hust.addrgeneration.beans.*;
import com.hust.addrgeneration.service.IPv6AddrService;
import com.hust.addrgeneration.service.IPv6Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<UserResponse> register(@RequestBody InfoBean userInfo) throws Exception {return iPv6AddrService.createUser(userInfo);}

    // 删除用户(支持批量)
    @DeleteMapping(value = "/user")
    public ResponseEntity<Response> deleteUser(@RequestParam("userContent") String userContent) throws Exception {return iPv6AddrService.deleteUser(userContent);}

    // 修改用户
    @PostMapping(value = "/user")
    public ResponseEntity<UserResponse> updateUser(@RequestBody InfoBean userInfo) throws Exception {return iPv6AddrService.updateUser(userInfo);}

    // 地址生成
    @PostMapping(value = "/address")
    public ResponseEntity<GenerateAddressResponse> generateAddress(@RequestBody GenerateAddress generateAddress) throws Exception { return iPv6AddrService.generateAddr(generateAddress);}

    @GetMapping(value = "/query")
    public ResponseEntity<QueryAddressResponse> queryAddress(@RequestParam("nid") String nid) throws Exception {return iPv6AddrService.queryAddress(nid);}

    // 地址溯源
    @GetMapping(value = "/address")
    public ResponseEntity<TraceAddressResponse> traceAddress(@RequestParam("queryAddress") String queryAddress) throws Exception {return iPv6AddrService.traceAddress(queryAddress);}

    // 地址删除
    @DeleteMapping(value = "/address")
    public ResponseEntity<Response> deleteAddress(@RequestParam("deleteAddress") String address) throws Exception {return iPv6AddrService.deleteAddress(address);}
}
