package com.hust.addrgeneration.service;

import com.hust.addrgeneration.beans.User;


public interface IPv6Service {
    public String ipv6AddrGeneration(User userInfo) throws Exception;
    public String ipv6AddrRebind(User userInfo);
    public String ipv6AddrUnbind(String userID);
    public String showAllBindTable();
}
