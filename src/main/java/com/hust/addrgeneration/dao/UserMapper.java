package com.hust.addrgeneration.dao;

import com.hust.addrgeneration.beans.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper {
    // register表
    void register(String nid, String password, String userID, String phoneNumber, String username);      // register表，注册用户(申请NID)
    User queryRegisterInfo(String nid);    // register表，获取nid对应的username、userID、password、phoneNumber
    User queryPhoneNumber(String phoneNumber);        // 查找phoneNumber对应的user
    void deleteUser(String nid);           // 删除nid对应的用户
    List<User> getAllRegisteredUsers();     // 获取全部用户

    // aid表
    void updateAID(String AID, String AIDnTH);      // timehash表，新增
    String queryAIDAIDnTH(String AID);       // timehash表，获取AID对应的aidnth


    // aid_nth表
    void updateAIDnTH(String aidnth, String prefix);     // aidnth表，新增
    String queryAIDnTHPrefix(String aidnth);      // aidnth表，获取aidnth对应的prefix


    // aid_trunc表
    void updateAIDTrunc(String address, String visibleAID, String hiddenAID, int timeDifference, String nid, long registerTime, String prefix);    // aid_trunc表，新增
    String queryAIDTruncHiddenAID(String visibleAID, int timeDifference);  // aid_trunc表，获取visibleAID、timeDifference对应的hiddenAID
    int queryAIDTruncTime(String address);       // aid_trunc表，获取address对应的timeDifference
    String queryAIDTrunc(String nid);       // 查询nid对应的address
    void truncateAIDTrunc();                // 截断地址表


    // key_info表
    String getIdeaKey(String timeHash, String addrGenIP);   // key表，获取密钥
    void updateKey(String addrGenIP, String ideaKey, String timeHash);      // key表，新增密钥

    // 联合查询
    List<User> getUsersByFilter(int offset, int limit, String content);       // 用户管理
    int getUserCountByFilter(String content);
}
