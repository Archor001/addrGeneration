package com.hust.addrgeneration.beans;

public class Query {
    private String queryAddress;
    private int prefixLength;
    public String getQueryAddress() { return queryAddress; }

    public void setQueryAddress(String queryAddress) { this.queryAddress = queryAddress; }

    public int getPrefixLength() { return prefixLength; }

    public void setPrefixLength(int prefixLength) { this.prefixLength = prefixLength; }
}
