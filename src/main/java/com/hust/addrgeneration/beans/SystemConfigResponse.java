package com.hust.addrgeneration.beans;

public class SystemConfigResponse extends Response{
    private SystemConfig config;
    public SystemConfigResponse(){};
    public SystemConfigResponse(int code, String msg, int status, SystemConfig config) {
        super(code, msg);
        this.config = config;
    }
    public SystemConfig getConfig() {return config;}
    public void setConfig(SystemConfig config) {this.config = config;}
}
