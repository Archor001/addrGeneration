package com.hust.addrgeneration.beans;

public class Address {
    private String nid;
    private String password;
    private String prefix;
    private String suffix;

    public String getNid() { return nid; }

    public void setNid(String nid) { this.nid = nid; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }


    public String getPrefix() { return prefix; }
    public void setPrefix(String prefix) { this.prefix = prefix; }

    public String getSuffix() { return suffix; }
    public void setSuffix(String suffix) { this.suffix = suffix; }
}
