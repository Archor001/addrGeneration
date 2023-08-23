package com.hust.addrgeneration.controller;

import com.alibaba.fastjson.JSONObject;
import com.hust.addrgeneration.beans.InfoBean;
import com.hust.addrgeneration.beans.NormalMsg;
import com.hust.addrgeneration.beans.QueryInfo;
import com.hust.addrgeneration.service.IPv6AddrService;
import com.hust.addrgeneration.service.IPv6Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @PutMapping(value = "/user/nid")
    public NormalMsg register(@RequestBody InfoBean userInfo) throws Exception {
        NormalMsg response = new NormalMsg();
        try {
            String NID = iPv6AddrService.getNID(userInfo);
            response.setCode(200);
            response.setMsg("用户注册成功！已成功分配");
            JSONObject data = new JSONObject();
            data.put("nid", NID);
            data.put("phoneNumber", userInfo.getPhoneNumber());
            data.put("userID", userInfo.getUserID());
            data.put("username", userInfo.getUsername());
            response.setInfo(data);
            return response;
        } catch (Exception e) {
            response.setCode(10002);
            response.setMsg(e.getMessage());
            return response;
        }
    }

    @PostMapping(value = "/user/address")
    public NormalMsg creatPort(@RequestBody InfoBean userInfo) throws Exception {
        NormalMsg resp = new NormalMsg();
        try {
            String addr = iPv6AddrService.createAddr(userInfo);
            resp.setCode(1);
            resp.setMsg(addr);
            return resp;
        } catch (Exception e) {
            resp.setCode(0);
            if (e.getMessage() == null) {
                resp.setMsg("用户尚未注册！请先注册");
            } else{
                resp.setMsg(e.getMessage());
            }
            return resp;
        }
    }

    @GetMapping(value = "/user/address")
    public NormalMsg queryAddr(@RequestParam InfoBean userInfo) throws Exception {
        QueryInfo resp = new QueryInfo();
        try {
            JSONObject data = iPv6AddrService.queryAddr(userInfo);
            resp.setCode(1);
            resp.setMsg("地址查询成功");
            resp.setPhoneNumber(data.getString("phoneNumber"));
            resp.setRegisterTime(data.getString("registerTime"));
            resp.setUsername(data.getString("username"));
            resp.setUserID(data.getString("userID"));
            return resp;
        } catch (Exception e) {
            resp.setCode(0);
            if (e.getMessage() == null)
                resp.setMsg("查询地址不存在！");
            else
                resp.setMsg(e.getMessage());
            return resp;
        }
    }
}
