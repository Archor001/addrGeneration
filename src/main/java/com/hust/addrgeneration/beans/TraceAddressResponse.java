package com.hust.addrgeneration.beans;

import com.hust.addrgeneration.utils.ErrorUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class TraceAddressResponse extends Response {
    private String registerTime;
    private User infoBean;
    public TraceAddressResponse(){};
    public TraceAddressResponse(int code, String msg, int status, String registerTime, User infoBean) {
        super(code, msg);
        this.registerTime = registerTime;
        this.infoBean = infoBean;
    }
    public String getRegisterTime() {return registerTime;}
    public void setRegisterTime(String registerTime) {this.registerTime = registerTime;}
    public User getInfoBean() {return infoBean;}
    public void setInfoBean(User infoBean) {this.infoBean = infoBean;}
    public ResponseEntity<TraceAddressResponse> responseError(int code){
        this.setCode(code);
        this.setMsg(ErrorUtils.message(code));
        return new ResponseEntity<>(this, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
