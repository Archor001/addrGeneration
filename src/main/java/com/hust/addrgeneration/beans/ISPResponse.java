package com.hust.addrgeneration.beans;

import com.hust.addrgeneration.utils.ErrorUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ISPResponse extends Response{
    private String isp;
    private int length;
    public ISPResponse(){};
    public ISPResponse(int code, String msg, String isp, int length){
        super(code, msg);
        this.isp = isp;
        this.length = length;
    }
    public void setIsp(String isp){
        this.isp = isp;
    }
    public String getIsp(){
        return this.isp;
    }
    public void setLength(int length){
        this.length = length;
    }
    public int getLength(){
        return this.length;
    }
}
