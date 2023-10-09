package com.hust.addrgeneration.service;

import com.hust.addrgeneration.beans.*;
import org.springframework.http.ResponseEntity;

public interface IPv6AddrService {
    ResponseEntity<GenerateAddressResponse> generateAddress(GenerateAddress generateAddress) throws Exception;
    ResponseEntity<QueryAddressResponse> queryAddress(String nid) throws Exception;
    ResponseEntity<TraceAddressResponse> traceAddress(String queryAddress) throws Exception;
    ResponseEntity<Response> deleteAddress(String address) throws Exception;
}
