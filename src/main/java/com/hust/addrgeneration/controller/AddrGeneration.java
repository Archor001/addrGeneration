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
    @PutMapping(value = "/admin/register")
    public ResponseEntity<GenerateAddressResponse> register(@RequestBody User userInfo) throws Exception {
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

    // 删除用户
    @DeleteMapping(value="/admin/user")
    public ResponseEntity<UserResponse> deleteUser(@RequestParam("nid") String nid) throws Exception{
        return userService.DeleteUser(nid);
    }

    // 地址生成
    @PostMapping(value = "/admin/address")
    public ResponseEntity<GenerateAddressResponse> createAddress(@RequestBody GenerateAddress addressInfo) throws Exception {
        return iPv6AddrService.createAddr(addressInfo);
    }

    // 地址查询
    @GetMapping(value = "/admin/address")
    public ResponseEntity<QueryAddressResponse> queryAddr(@RequestParam("queryAddress") String queryAddress) throws Exception {
        QueryAddress queryAddressInfo = new QueryAddress();
        queryAddressInfo.setQueryAddress(queryAddress);
        return iPv6AddrService.queryAddr(queryAddressInfo);
    }

    // 修改ISP地址前缀（自动重新生成地址）
    @PostMapping(value = "/admin/isp")
    public ResponseEntity<Response> updateISP(@RequestBody ISP isp) throws Exception {
        return iPv6AddrService.updateISP(isp);
    }

    // 获取ISP地址前缀
    @GetMapping(value="/admin/isp")
    public ResponseEntity<ISPResponse> getISP() throws Exception {
        return iPv6AddrService.getISP();
    }

    // 手动重新生成地址
    @PostMapping(value = "/admin/regen/address")
    public ResponseEntity<Response> regenerateAddress(@RequestBody ISP isp) throws Exception {
        return iPv6AddrService.regenAddress(isp);
    }
}
