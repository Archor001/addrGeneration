package com.hust.addrgeneration.dao;

import com.hust.addrgeneration.beans.KeyInfo;
import com.hust.addrgeneration.beans.QueryInfo;
import com.hust.addrgeneration.beans.QueryKeyInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper {
    int register(String nid, String password, String userID, String phoneNumber, String username);
    int updateKey(KeyInfo keyInfo);
    int updateAIDnTH(String aidnth, String prefix);
    int updateTimeHash(String AID, String AIDnTH);
    int updateTruncAID(String address, String visibleAID, String hiddenAID, int timeDifference);
    String getKey(String nid);
    String getAIDnTH(String AID);
    String getAIDnTHPrefix(String aidnth);
    String getIdeaKey(String addrGenIP, String timeHash);
    QueryInfo queryAddrInfo(String nid);
    String getTruncAID(String visibleAID, int timeDifference);
    int getTruncTime(String address);
}
