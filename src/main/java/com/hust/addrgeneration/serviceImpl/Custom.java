package com.hust.addrgeneration.serviceImpl;

import com.hust.addrgeneration.beans.ISP;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(  prefix = "custom" )
public class Custom {
    private String admin;
    private String password;
    public void setAdmin(String admin) { this.admin = admin; }
    public String getAdmin() { return admin; }
    public void setPassword(String password) { this.password = password; }
    public String getPassword() { return password; }
}
