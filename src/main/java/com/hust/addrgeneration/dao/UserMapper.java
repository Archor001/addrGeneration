package com.hust.addrgeneration.dao;

import com.hust.addrgeneration.beans.InfoBean;
import com.hust.addrgeneration.beans.KeyInfo;
import com.hust.addrgeneration.beans.QueryInfo;
import com.hust.addrgeneration.beans.QueryKeyInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper {
    // register表
    void register(String nid, String password, String username, String phoneNumber, String name, String emailAddress, int role);
    void deleteUser(String userContent);
    void updateUser(InfoBean infoBean);
    InfoBean getUser(String nid);
    String getPasswordFromNID(String nid);

    // keyinfo表
    int updateKey(KeyInfo keyInfo);
    String getIdeaKey(String addrGenIP, String timeHash);

    // aidinfo表
    int updateAID(String aid, String prefix);
    String getAIDPrefix(String AID);

    // timehash表
    int updateTimeHash(String AID, String AIDnTH);
    String getAIDnTH(String AID);
}
