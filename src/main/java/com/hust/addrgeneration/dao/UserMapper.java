package com.hust.addrgeneration.dao;

import com.hust.addrgeneration.beans.KeyInfo;
import com.hust.addrgeneration.beans.QueryInfo;
import com.hust.addrgeneration.beans.QueryKeyInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper {
    int register(String NID, String password, String userID, String phoneNumber, String userName);
    int updateKey(KeyInfo keyInfo);
    int updateAID(String aid, String prefix);
    int updateTimeHash(String AID, String AIDnTH);
    String getKey(String NID);
    String getAIDnTH(String AID);
    String getPrefix(String AID);
    String getIdeaKey(String addrGenIP, String timeHash);
    QueryInfo queryAddrInfo(String NID);
}
