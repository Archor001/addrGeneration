package com.hust.addrgeneration.controller;

import com.hust.addrgeneration.beans.*;
import com.hust.addrgeneration.service.IPv6AddrService;
import com.hust.addrgeneration.service.SystemService;
import com.hust.addrgeneration.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AddrGeneration {
    private final IPv6AddrService iPv6AddrService;
    private final UserService userService;
    private final SystemService systemService;

    @Autowired
    public AddrGeneration(IPv6AddrService iPv6AddrService, UserService userService, SystemService systemService) {
        this.iPv6AddrService = iPv6AddrService;
        this.userService = userService;
        this.systemService = systemService;
    }

    // 登录
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User userInfo) throws Exception {return userService.Login(userInfo);}

    // 批量获取用户
    @GetMapping(value="/users")
    public ResponseEntity<?> manageUser(
            @RequestParam("offset") int offset,
            @RequestParam("limit") int limit,
            @RequestParam("content") String content) throws Exception {
        return userService.FilterUsers(offset, limit, content);
    }

    // 创建用户
    @PutMapping(value = "/user")
    public ResponseEntity<?> register(@RequestBody User userInfo) throws Exception {return userService.createUser(userInfo);}

    // 停用用户
    @PostMapping(value = "/user/suspend")
    public ResponseEntity<?> suspendUser(@RequestBody String nid) throws Exception {return userService.suspendUser(nid);}

    // 删除用户(支持批量)
    @DeleteMapping(value = "/user")
    public ResponseEntity<?> deleteUser(@RequestParam("userContent") String userContent) throws Exception {return userService.deleteUser(userContent);}

    // 修改用户
    @PostMapping(value = "/user")
    public ResponseEntity<?> updateUser(@RequestBody User userInfo) throws Exception {return userService.updateUser(userInfo);}

    // 批量获取地址
    @GetMapping(value = "/addresses")
    public ResponseEntity<?> filterAddress(
            @RequestParam("offset") int offset,
            @RequestParam("limit") int limit,
            @RequestParam("content") String content) throws Exception {
        return iPv6AddrService.filterAddress(offset,limit,content);
    }

    // 地址生成
    @PostMapping(value = "/address")
    public ResponseEntity<?> generateAddress(@RequestBody GenerateAddress generateAddress) throws Exception { return iPv6AddrService.generateAddress(generateAddress);}

    // 地址查询
    @GetMapping(value = "/query")
    public ResponseEntity<?> queryAddress(@RequestParam("nid") String nid) throws Exception {return iPv6AddrService.queryAddress(nid);}

    // 地址溯源
    @GetMapping(value = "/address")
    public ResponseEntity<?> traceAddress(@RequestParam("queryAddress") String queryAddress) throws Exception {return iPv6AddrService.traceAddress(queryAddress);}

    // 地址停用
    @PostMapping(value = "/address/suspend")
    public ResponseEntity<?> suspendAddress(@RequestBody String address) throws Exception {return iPv6AddrService.suspendAddress(address);}

    // 地址删除
    @DeleteMapping(value = "/address")
    public ResponseEntity<?> deleteAddress(@RequestParam("deleteAddress") String address) throws Exception {return iPv6AddrService.deleteAddress(address);}

    // 系统配置
    @GetMapping(value = "/system")
    public ResponseEntity<?> getSystemConfig() throws Exception {return systemService.getSystemConfig();}
    @PostMapping(value = "/system")
    public ResponseEntity<?> updateSystemConfig(@RequestBody SystemConfig config) throws Exception {return systemService.updateSystemConfig(config);}
}
