package com.hust.addrgeneration.service;

import com.hust.addrgeneration.beans.InfoBean;
import org.springframework.stereotype.Service;


public interface IPv6Service {
    public String ipv6AddrGeneration(InfoBean userInfo) throws Exception;
    public String ipv6AddrRebind(InfoBean userInfo);
    public String ipv6AddrUnbind(String userID);
    public String showAllBindTable();
}
