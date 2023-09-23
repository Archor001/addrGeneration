package com.hust.addrgeneration.beans;

public class ISPFormat {
    private String isp;
    private int length;
    public ISPFormat(String isp, int length){
        this.isp = isp;
        this.length = length;
    }
    public String getIsp() {
        return this.isp;
    }
    public void setIsp(String isp) {
        this.isp = isp;
    }
    public int getLength() {
        return this.length;
    }
    public void setLength(int length) {
        this.length = length;
    }
}
