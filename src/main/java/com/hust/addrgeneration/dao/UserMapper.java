package com.hust.addrgeneration.dao;

import com.hust.addrgeneration.beans.Address;
import com.hust.addrgeneration.beans.QueryAIDTrunc;
import com.hust.addrgeneration.beans.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper {
    // user表
    void register(String nid, String password, String userID, String phoneNumber, String username, int status);      // register表，注册用户(申请NID)
    User queryRegisterInfo(String nid);    // register表，获取nid对应的username、userID、password、phoneNumber
    User queryPhoneNumber(String phoneNumber);        // 查找phoneNumber对应的user
    void updateUser(User user);    // 修改用户
    void deleteUser(String phoneNumber);           // 标记删除phoneNumber对应的用户,标记status=3
    List<User> getAllRegisteredUsers();     // 获取全部用户

    // aid_info表
    void updateAID(String aid, String aidnth, String prefix);      // aid_info，新增
    String queryAIDnTH(String aid);       // aid_info，获取AID对应的aidnth
    String queryPrefix(String aidnth);      // aid_info，获取aidnth对应的prefix


    // aid_trunc表
    void updateAIDTrunc(String address, String aid, String visibleAID, String hiddenAID, int timeDifference, String phoneNumber, long registerTime, String prefix, int status);    // aid_trunc表，新增
    String queryAIDTruncHiddenAID(String visibleAID, int timeDifference);  // aid_trunc表，获取visibleAID、timeDifference对应的hiddenAID
    QueryAIDTrunc queryAIDTruncResult(String aid);       // aid_trunc表，获取address对应的timeDifference
    List<Address> queryAIDTruncAddress(String phoneNumber);       // 查询phoneNumber对应的address
    void truncateAIDTrunc();                // 截断地址表
    void suspendAIDTrunc();


    // key_info表
    String getIdeaKey(String timeHash, String addrGenIP);   // key表，获取密钥
    void updateKey(String addrGenIP, String ideaKey, String timeHash);      // key表，新增密钥

    // 联合查询
    List<User> getUsersByFilter(int offset, int limit, String content);       // 用户管理
    int getUserCountByFilter(String content);
    List<Address> getAddressesByFilter(int offset, int limit, String content);     // 地址管理
    int getAddressCountByFilter(String content);
}
