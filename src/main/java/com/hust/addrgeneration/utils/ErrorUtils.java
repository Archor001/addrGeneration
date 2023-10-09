package com.hust.addrgeneration.utils;

import com.hust.addrgeneration.beans.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ErrorUtils {

    public static String message(int code) {
        switch (code) {
            case 10001:
                return "需要登录";
            case 10002:
                return "密码错误";
            case 10003:
                return "创建用户失败";
            case 10004:
                return "删除用户失败";
            case 10005:
                return "无效手机号码";
            case 10006:
                return "无效角色";
            case 10007:
                return "无效邮箱";
            case 10008:
                return "修改用户失败";
            case 10009:
                return "NID不存在";
            case 10010:
                return "请勿频繁生成地址";
            case 10011:
                return "地址生成失败";
            case 10012:
                return "地址溯源失败";
            case 10013:
                return "获取密钥出错！密钥集为空";
            case 10014:
                return "解密出错";
            case 10015:
                return "地址查询失败";
            case 10016:
                return "地址删除失败";
            case 10017:
                return "批量获取用户失败";
            default:
                return "未知错误";
        }
    }
}