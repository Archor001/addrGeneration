package com.hust.addrgeneration.service;

import com.hust.addrgeneration.beans.*;
import org.springframework.http.ResponseEntity;

public interface IPv6AddrService {
    ResponseEntity<?> filterAddress(int offset, int limit, String content) throws Exception;
    ResponseEntity<?> generateAddress(GenerateAddress generateAddress) throws Exception;
    ResponseEntity<?> queryAddress(String nid) throws Exception;
    ResponseEntity<?> traceAddress(String queryAddress) throws Exception;
    ResponseEntity<?> deleteAddress(String address) throws Exception;
    ResponseEntity<?> suspendAddress(Address address) throws Exception;
}
