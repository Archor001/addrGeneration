package com.hust.addrgeneration.beans;

import org.springframework.stereotype.Component;

@Component
public class KeyInfo {
    private String addrGenIP;
    private String ideaKey;
    private String timeHash;

    public String getTimeHash() {
        return timeHash;
    }

    public void setTimeHash(String timeHash) {
        this.timeHash = timeHash;
    }

    public String getAddrGenIP() {
        return addrGenIP;
    }

    public void setAddrGenIP(String addrGenIP) {
        this.addrGenIP = addrGenIP;
    }

    public String getIdeaKey() {
        return ideaKey;
    }

    public void setIdeaKey(String ideaKey) {
        this.ideaKey = ideaKey;
    }

}
