package com.hust.addrgeneration.serviceImpl;

import com.hust.addrgeneration.beans.Response;
import com.hust.addrgeneration.beans.SystemConfig;
import com.hust.addrgeneration.beans.SystemConfigResponse;
import com.hust.addrgeneration.dao.UserMapper;
import com.hust.addrgeneration.service.SystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SystemServiceImpl implements SystemService {
    private final UserMapper userMapper;
    @Autowired
    private Custom custom;
    public float addressTimeout = 24;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    public SystemServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    // 获取配置
    @Override
    public ResponseEntity<?> getSystemConfig() throws Exception {
        SystemConfigResponse response = new SystemConfigResponse();

        SystemConfig config = new SystemConfig();
        config.setAddressTimeout(addressTimeout);
        // others

        response.setCode(0);
        response.setMsg("success");
        response.setConfig(config);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 修改配置
    @Override
    public ResponseEntity<?> updateSystemConfig(SystemConfig config) throws Exception {
        Response response = new Response();

        addressTimeout = config.getAddressTimeout();;
        // others

        response.setCode(0);
        response.setMsg("success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
