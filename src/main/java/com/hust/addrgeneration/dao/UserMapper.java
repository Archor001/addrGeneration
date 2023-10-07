package com.hust.addrgeneration.dao;

import com.hust.addrgeneration.beans.KeyInfo;
import com.hust.addrgeneration.beans.QueryInfo;
import com.hust.addrgeneration.beans.QueryKeyInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper {
    void register(String NID, String password, String username, String phoneNumber, String name, String emailAddress, int role);
    void delete(String userContent);
    int updateKey(KeyInfo keyInfo);
    int updateAID(String aid, String prefix);
    int updateTimeHash(String AID, String AIDnTH);
    String getKey(String NID);
    String getAIDnTH(String AID);
    String getPrefix(String AID);
    String getIdeaKey(String addrGenIP, String timeHash);
    QueryInfo queryAddrInfo(String NID);
}
