package com.hust.addrgeneration.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class NetUtils {
    public static String matchSubnet(JSONObject subnets, String prefix) {       // prefix是ISP地址前缀
        String prefixAddress = prefix.replace(":","");       // 去掉ISP地址前缀的冒号
        String rtn = "";
        JSONArray subnetArray = subnets.getJSONArray("subnets");
        for(int i = 0; i < subnetArray.size(); i++ ){
            JSONObject subnet = subnetArray.getJSONObject(i);                  // 单个子网信息
            String subnetCIDR = subnet.getString("cidr");                 // 子网的CIDR
            int pos = subnetCIDR.indexOf("/");
            int subnetLength = Integer.parseInt(subnetCIDR.substring(pos+1));                   // 子网前缀长度
            if(subnetLength > prefixAddress.length() * 4) continue;                                       //  如果子网前缀长度超过了ISP地址前缀的长度，则ISP肯定不在此子网下
            String subnetAddress = subnetCIDR.substring(0, pos - 3).replace(":","");     // 子网前缀地址
            if(prefixAddress.startsWith(subnetAddress))                                                    // 如果ISP地址前缀的前缀是子网地址，则返回此子网的id
                return subnet.getString("id");
        }
        return rtn;
    }
}
