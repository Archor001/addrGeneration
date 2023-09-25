package com.hust.addrgeneration.controller;

import com.alibaba.fastjson.JSONObject;
import com.hust.addrgeneration.beans.InfoBean;
import com.hust.addrgeneration.beans.NormalMsg;
import com.hust.addrgeneration.beans.QueryInfo;
import com.hust.addrgeneration.beans.RegisterInfo;
import com.hust.addrgeneration.service.IPv6AddrService;
import com.hust.addrgeneration.service.IPv6Service;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    public RegisterInfo register(@RequestBody InfoBean userInfo) throws Exception {
        RegisterInfo backHtml = new RegisterInfo();
        try {
            String NID = iPv6AddrService.getNID(userInfo);
            backHtml.setStatus(1);
            backHtml.setMessage("用户注册成功！已成功分配");
            JSONObject info = new JSONObject();
            info.put("nid", NID);
            info.put("userID", userInfo.getUserID());
            info.put("name", userInfo.getName());
            backHtml.setInfo(info);
            return backHtml;
        } catch (Exception e) {
            backHtml.setStatus(0);
            backHtml.setMessage(e.getMessage());
            return backHtml;
        }
    }

    // 删除用户
    @DeleteMapping(value = "/user")
    public NormalMsg deleteUser(@RequestBody InfoBean userInfo) throws Exception {
        NormalMsg backHtml = new NormalMsg();
        try {
            String result = iPv6AddrService.deleteUser(userInfo);
            backHtml.setStatus(1);
            backHtml.setMessage(result);
            return backHtml;
        } catch (Exception e) {
            backHtml.setStatus(0);
            if (e.getMessage() == null) {
                backHtml.setMessage("用户删除失败");
            } else{
                backHtml.setMessage(e.getMessage());
            }
            return backHtml;
        }
    }

    // 修改用户
    @PostMapping(value = "/user")
    public NormalMsg updateUser(@RequestBody InfoBean userInfo) throws Exception {
        return null;
    }

    // 地址生成
    @PostMapping(value = "/address")
    public NormalMsg creatPort(@RequestBody InfoBean userInfo) throws Exception {
        NormalMsg backHtml = new NormalMsg();
        try {
            String addr = iPv6AddrService.generateAddr(userInfo);
            backHtml.setStatus(1);
            backHtml.setMessage(addr);
            return backHtml;
        } catch (Exception e) {
            backHtml.setStatus(0);
            if (e.getMessage() == null) {
                backHtml.setMessage("用户尚未注册！请先注册");
            } else{
                backHtml.setMessage(e.getMessage());
            }
            return backHtml;
        }
    }

    // 地址查询（溯源）
    @GetMapping(value = "/address")
    public NormalMsg queryAddr(@RequestBody InfoBean userInfo) throws Exception {
        QueryInfo backHtml = new QueryInfo();
        try {
            JSONObject info = iPv6AddrService.queryAddr(userInfo);
            backHtml.setStatus(1);
            backHtml.setMessage("地址查询成功");
            backHtml.setPhoneNumber(info.getString("phoneNumber"));
            backHtml.setRegisterTime(info.getString("registerTime"));
            backHtml.setUserName(info.getString("userName"));
            backHtml.setUserID(info.getString("userID"));
            return backHtml;
        } catch (Exception e) {
            backHtml.setStatus(0);
            if (e.getMessage() == null)
                backHtml.setMessage("查询地址不存在！");
            else
                backHtml.setMessage(e.getMessage());
            return backHtml;
        }
    }

    // 地址删除
}
