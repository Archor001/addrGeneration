package com.hust.addrgeneration.service;

import com.alibaba.fastjson.JSONObject;
import com.hust.addrgeneration.beans.InfoBean;
import com.hust.addrgeneration.beans.QueryInfo;
import org.springframework.stereotype.Service;

public interface IPv6AddrService {
    String getNID(InfoBean infoBean);
    String createAddr(InfoBean infoBean) throws Exception;
    JSONObject queryAddr(InfoBean infoBean) throws Exception;
}
