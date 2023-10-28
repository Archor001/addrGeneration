package com.hust.addrgeneration.service;

import com.hust.addrgeneration.beans.*;
import org.springframework.http.ResponseEntity;

public interface IPv6AddrService {
    ResponseEntity<GenerateAddressResponse> register(User infoBean) throws Exception;
    ResponseEntity<GenerateAddressResponse> createAddr(GenerateAddress address) throws Exception;
    ResponseEntity<TraceAddressResponse> traceAddr(String queryAddress) throws Exception;
    ResponseEntity<QueryAddressResponse> queryAddr(String phoneNumber) throws Exception;
    ResponseEntity<Response> updateISP(ISP isp) throws Exception;
    ResponseEntity<ISPResponse> getISP() throws Exception;
    ResponseEntity<Response> regenAddress() throws Exception;
    ResponseEntity<?> getConfig() throws Exception;
}
