package com.hust.addrgeneration.service;

import com.alibaba.fastjson.JSONObject;
import com.hust.addrgeneration.beans.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

public interface IPv6AddrService {
    ResponseEntity<RegisterResponse> createUser(InfoBean infoBean) throws Exception;
    ResponseEntity<Response> deleteUser(InfoBean infoBean) throws Exception;
    ResponseEntity<Response> updateUser(InfoBean infoBean) throws Exception;
    ResponseEntity<GenerateAddressResponse> generateAddr(InfoBean infoBean) throws Exception;
    ResponseEntity<QueryAddressResponse> queryAddr(InfoBean infoBean) throws Exception;
}
