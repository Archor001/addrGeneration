package com.hust.addrgeneration.dao;

import com.hust.addrgeneration.beans.Address;
import com.hust.addrgeneration.beans.User;
import com.hust.addrgeneration.beans.KeyInfo;
import com.hust.addrgeneration.beans.UserAddress;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper {
    // register表
    void register(String nid, String password, String username, String phoneNumber, String name, String emailAddress, int role, int status);
    void suspendUser(String userContent);       // 停用用户，设置status字段为2
    void deleteUser(String userContent);        // 标记删除用户，设置status字段为3
    void updateUser(User infoBean);
    User getUser(String nid);
    String getPasswordFromNID(String nid);

    // keyinfo表
    int updateKey(KeyInfo keyInfo);
    String getIdeaKey(String addrGenIP, String timeHash);

    // aidinfo表
    void updateAID(String aidnth, String prefix, String aid, String nid, long registerTime, String address, int status);
    List<Address> getAddress(String nid);
    String getAIDnTHPrefix(String aidnth);
    String getAIDnTH(String aid);
    int getAIDStatus(String aid);
    void suspendAID(String aid);            // 停用地址（设置status字段为2 ）
    void deleteAID(String aid);             // 标记删除地址（设置status字段为3）

    // 联合查询
    List<UserAddress> getUsersByFilter(int offset, int limit, String content);
    int getUserCountByFilter(String content);
    List<Address> getAddressesByFilter(int offset, int limit, String content);
    int getAddressCountByFilter(String content);
}
