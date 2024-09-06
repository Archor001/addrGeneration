package com.hust.addrgeneration.service;

import com.hust.addrgeneration.beans.*;
import org.springframework.http.ResponseEntity;

public interface IPv6AddrService {
    ResponseEntity<?> register(User infoBean) throws Exception;
    ResponseEntity<?> updateUser(User user) throws Exception;
    ResponseEntity<?> createAddr(GenerateAddress address) throws Exception;
    ResponseEntity<?> traceAddr(String queryAddress) throws Exception;
    ResponseEntity<?> queryAddr(String phoneNumber) throws Exception;
    ResponseEntity<?> filterAddress(int offset, int limit, String content) throws Exception;
    ResponseEntity<?> deleteAddr(String address) throws Exception;
    ResponseEntity<?> updateISP(ISP isp) throws Exception;
    ResponseEntity<?> getISP() throws Exception;
    ResponseEntity<?> regenAddress() throws Exception;
    ResponseEntity<?> getConfig() throws Exception;
    ResponseEntity<?> setSyncGap(float gap) throws Exception;
}
