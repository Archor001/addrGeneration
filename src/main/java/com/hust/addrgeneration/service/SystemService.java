package com.hust.addrgeneration.service;

import com.hust.addrgeneration.beans.SystemConfig;
import org.springframework.http.ResponseEntity;

public interface SystemService {
    ResponseEntity<?> getSystemConfig() throws Exception;
    ResponseEntity<?> updateSystemConfig(SystemConfig config) throws Exception;
}
