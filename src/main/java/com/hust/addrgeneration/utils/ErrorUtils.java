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
                return "注册用户失败";
            case 10003:
                return "地址生成失败";
            case 10004:
                return "手机号已被注册";
            case 10005:
                return "密码错误";
            case 10006:
                return "请注册此NID";
            case 10007:
                return "获取密钥集失败！密钥集为空";
            case 10008:
                return "解密失败";
            case 10009:
                return "加密失败";
            case 10010:
                return "获取用户失败";
            case 10011:
                return "此手机号已经创建了IPv6地址";
            case 10012:
                return "无效手机号码";
            case 10013:
                return "删除用户失败";
            case 10014:
                return "此NID对应不存在的用户";
            case 10015:
                return "手机号已经被注册";
            case 10016:
                return "待溯源的IPv6地址不存在";
            case 10017:
                return "此手机号还未生成NID";
            case 10018:
                return "请在平台事先创建好ISP地址聚合前缀";
            case 10019:
                return "ISP地址聚合前缀格式无效";
            case 10020:
                return "重新生成地址失败";
            case 10021:
                return "批量获取地址失败";
            case 10022:
                return "此手机号未生成对应的IPv6地址（或者地址已被删除）";
            case 10023:
                return "查询地址失败";
            default:
                return "Unknown";
        }
    }
}
