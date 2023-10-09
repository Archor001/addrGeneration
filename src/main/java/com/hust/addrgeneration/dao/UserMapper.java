package com.hust.addrgeneration.dao;

import com.hust.addrgeneration.beans.User;
import com.hust.addrgeneration.beans.KeyInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper {
    // register表
    void register(String nid, String password, String username, String phoneNumber, String name, String emailAddress, int role);
    void deleteUser(String userContent);
    void updateUser(User infoBean);
    User getUser(String nid);
    String getPasswordFromNID(String nid);

    // keyinfo表
    int updateKey(KeyInfo keyInfo);
    String getIdeaKey(String addrGenIP, String timeHash);

    // aidinfo表
    void updateAID(String aidnth, String prefix, String aid, String nid);
    String getAIDnTHPrefix(String aidnth);
    String getAIDnTH(String aid);
    String getAID(String nid);
    void deleteAID(String aid);

    // 联合查询
    List<User> getUsersByFilter(int offset, int limit, String content);
    int getUserCountByFilter(String content);
}
