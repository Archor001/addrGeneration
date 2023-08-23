package com.hust.addrgeneration.service;

import com.alibaba.fastjson.JSONObject;
import com.hust.addrgeneration.beans.*;
import org.springframework.http.ResponseEntity;

public interface IPv6AddrService {
    ResponseEntity<UserResponse> registerNID(User infoBean) throws Exception;
    ResponseEntity<AddressResponse> createAddr(Address address) throws Exception;
    ResponseEntity<QueryResponse> queryAddr(Query query) throws Exception;
}
