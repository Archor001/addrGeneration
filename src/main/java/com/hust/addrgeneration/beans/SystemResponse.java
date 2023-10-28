package com.hust.addrgeneration.beans;

public class SystemResponse extends Response{
    private ISP isp;
    private float syncGap;
    public SystemResponse(){};
    public SystemResponse(int code, String msg, ISP isp, float syncGap){
        super(code,msg);
        this.isp = isp;
        this.syncGap = syncGap;
    }
    public void setIsp(ISP isp) {this.isp = isp;}
    public ISP getIsp() {return isp;}
    public void setSyncGap(float syncGap) {this.syncGap = syncGap;}
    public float getSyncGap() {return syncGap;}
}
