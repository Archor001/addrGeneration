package com.hust.addrgeneration.controller;

import com.hust.addrgeneration.beans.*;
import com.hust.addrgeneration.service.IPv6AddrService;
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
    public ResponseEntity<?> login(@RequestBody User userInfo) throws Exception {
        return userService.Login(userInfo);
    }

    // 用户注册(申请NID)
    @PutMapping(value = "/admin/user")
    public ResponseEntity<?> register(@RequestBody User userInfo) throws Exception {
        return iPv6AddrService.register(userInfo);
    }

    // 批量获取用户
    @GetMapping(value="/admin/user")
    public ResponseEntity<?> manageUser(
            @RequestParam("offset") int offset,
            @RequestParam("limit") int limit,
            @RequestParam("content") String content) throws Exception {
        return userService.FilterUsers(offset, limit, content);
    }

    // 删除用户
    @DeleteMapping(value="/admin/user")
    public ResponseEntity<?> deleteUser(@RequestParam("phoneNumber") String phoneNumber) throws Exception{
        return userService.DeleteUser(phoneNumber);
    }

    // 地址生成
    @PostMapping(value = "/admin/address")
    public ResponseEntity<?> createAddress(@RequestBody GenerateAddress addressInfo) throws Exception {
        return iPv6AddrService.createAddr(addressInfo);
    }

    // 地址查询
    @GetMapping(value = "/admin/query")
    public ResponseEntity<?> queryAddress(@RequestParam("phoneNumber") String phoneNumber) throws Exception {
        return iPv6AddrService.queryAddr(phoneNumber);
    }

    // 地址溯源
    @GetMapping(value = "/admin/address")
    public ResponseEntity<?> traceAddress(@RequestParam("queryAddress") String queryAddress) throws Exception {
        return iPv6AddrService.traceAddr(queryAddress);
    }

    // 修改ISP地址前缀（自动重新生成地址）
    @PostMapping(value = "/admin/isp")
    public ResponseEntity<?> updateISP(@RequestBody ISP isp) throws Exception {
        return iPv6AddrService.updateISP(isp);
    }

    // 获取ISP地址前缀
    @GetMapping(value="/admin/isp")
    public ResponseEntity<?> getISP() throws Exception {
        return iPv6AddrService.getISP();
    }

    // 手动重新生成地址
    @PostMapping(value = "/admin/regen/address")
    public ResponseEntity<?> regenerateAddress() throws Exception {
        return iPv6AddrService.regenAddress();
    }

    // 获取系统配置
    @GetMapping(value = "/admin/system")
    public ResponseEntity<?> getSystemConfig() throws Exception {
        return iPv6AddrService.getConfig();
    }

    // 修改用户同步时间
    @PostMapping(value = "/admin/system")
    public ResponseEntity<?> setGap(@RequestBody float syncGap) throws Exception {
        return iPv6AddrService.setSyncGap(syncGap);
    }
}
