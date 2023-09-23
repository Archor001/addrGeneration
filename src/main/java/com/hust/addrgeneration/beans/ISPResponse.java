package com.hust.addrgeneration.beans;

public class ISPResponse extends Response{
    private String isp;
    private int length;
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
