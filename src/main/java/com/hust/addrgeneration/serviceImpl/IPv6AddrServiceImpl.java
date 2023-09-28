package com.hust.addrgeneration.serviceImpl;

import com.alibaba.fastjson.JSONObject;
import com.hust.addrgeneration.beans.*;
import com.hust.addrgeneration.dao.UserMapper;
import com.hust.addrgeneration.encrypt.IDEAUtils;
import com.hust.addrgeneration.service.IPv6AddrService;
import com.hust.addrgeneration.utils.ConvertUtils;
import com.hust.addrgeneration.utils.EncDecUtils;
import com.hust.addrgeneration.utils.HashUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.sound.sampled.Line;
import java.math.BigInteger;
import java.time.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class IPv6AddrServiceImpl implements IPv6AddrService {
    private final UserMapper userMapper;
    private int portCount = 0;
    private int stamp = 10;
    private static final Logger logger = LoggerFactory.getLogger(IPv6AddrServiceImpl.class);

    @Autowired
    public IPv6AddrServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public String getNID(InfoBean infoBean) throws Exception {
        String userID = infoBean.getUserID();
        String password = infoBean.getPassword();
        String phoneNumber = infoBean.getPhoneNumber();
        String userName = infoBean.getName();

        // step1. Calculate NID with user's information
        String encryptStr = userID + phoneNumber + userName;
        String hashStr = HashUtils.SM3Hash(encryptStr);
        String userPart = ConvertUtils.hexStringToBinString(hashStr).substring(0,38);
        String userType = userID.substring(0,1);
        String organizationPart = "";
        switch (userType) {
            case "U" :
                organizationPart = "00";
                break;

            case "M" :
                organizationPart = "01";
                break;

            case "D" :
                organizationPart = "10";
                break;
            default:
                organizationPart = "11";
                break;
        }
    
        String NID = ConvertUtils.binStringToHexString(userPart + organizationPart);
        QueryInfo info = userMapper.queryAddrInfo(NID);
        if(info != null){
            throw new Exception("此用户已经注册NID");
        }
        userMapper.register(NID,password,userID,phoneNumber, userName);

        return NID;

    }

    @Override
    public ResponseEntity<RegisterResponse> createUser(InfoBean infoBean) throws Exception {
        RegisterResponse response = new RegisterResponse();
        // TODO
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 删除用户
    @Override
    public ResponseEntity<Response> deleteUser(InfoBean infoBean) throws  Exception {
        Response response = new Response();
        // TODO
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 修改用户
    @Override
    public ResponseEntity<Response> updateUser(InfoBean infoBean) throws  Exception {
        Response response = new Response();
        // TODO
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 地址生成
    @Override
    public ResponseEntity<GenerateAddressResponse> generateAddr(InfoBean infoBean) throws Exception {
        GenerateAddressResponse response = new GenerateAddressResponse();
        return new ResponseEntity<>(response, HttpStatus.OK);

//        String NID = infoBean.getNid();
//        String password = infoBean.getPassword();
//        // step1. check NID and password
//        /*
//         if the NID isn't in the database, return the information tells user to register a NID
//         if the NID isn't match the password, return the wrong password information
//         */
//        String passwordFromDB = userMapper.getKey(NID);
//        if (!passwordFromDB.equals(password)) {
//            throw new Exception("密码错误，请重新输入！");
//        }
//
//        // step2. Calculate the time information
//        LocalDateTime localDateTime1 = LocalDateTime.now();
//        LocalDateTime localDateTime2 = LocalDateTime.of(localDateTime1.getYear(), 1, 1, 0, 0, 0);
//
//        long currentTime = localDateTime1.toEpochSecond(ZoneOffset.of("+8"));
//        long baseTime = localDateTime2.toEpochSecond(ZoneOffset.of("+8"));
//
//        int timeDifference = (int) (( currentTime - baseTime ) / stamp);
//        String timeInformation = ConvertUtils.decToBinString(timeDifference, 24);
//        String rawAID = NID + ConvertUtils.binStringToHexString(timeInformation);
//
//        // step3. Generate AID-noTimeHash(aka AID_nTH) with UID and time information
//        String preAID = EncDecUtils.ideaEncrypt(rawAID, EncDecUtils.ideaKey);
//        String str1 = preAID.substring(0,16);
//        String str2 = preAID.substring(16,32);
//
//        BigInteger big1 = new BigInteger(str1, 16);
//        BigInteger big2 = new BigInteger(str2, 16);
//        String AIDnTH = big1.xor(big2).toString(16);
//
//        String prefix = userMapper.getPrefix(AIDnTH);
//        if(prefix != null){
//            throw new Exception("请勿频繁生成地址");
//        }
//        userMapper.updateAID(AIDnTH, big1.toString(16));
//
//        // step4. Generate AID-withTimeHash(aka AID) with AIDnTH and time-Hash
//        LocalDateTime localDateTime3 = LocalDateTime.of(localDateTime1.getYear(),localDateTime1.getMonth(),localDateTime1.getDayOfMonth(),localDateTime1.getHour(),0,0);
//        long nearestTimeHour = localDateTime3.toEpochSecond(ZoneOffset.of("+8"));
//        int timeDifference2 = (int) (nearestTimeHour - baseTime);
//        String timeHash = EncDecUtils.md5Encrypt16(ConvertUtils.decToHexString(timeDifference2,10));
//
//        BigInteger big3 = new BigInteger(AIDnTH,16);
//        BigInteger big4 = new BigInteger(timeHash, 16);
//        String AID = String.format("%016x", big3.xor(big4));
//        userMapper.updateTimeHash(AID, AIDnTH);
//
//        StringBuilder suffix = new StringBuilder();
//        for (int i = 0; i < AID.length(); i+=4) {
//            suffix.append(AID, i, i + 4).append(":");
//        }
//        String asPrefix = "2001:250:4000:4507:";
//        return asPrefix + suffix.substring(0,suffix.length()-1);
    }

    // 地址查询(溯源)
    @Override
    public ResponseEntity<QueryAddressResponse> queryAddr(InfoBean infoBean) throws Exception {
        QueryAddressResponse response = new QueryAddressResponse();
        return new ResponseEntity<>(response, HttpStatus.OK);
//
//        // step1. use prefix of the IPv6-address and calculate time-Hash to get key
//        String queryAddress = infoBean.getQueryAddress();
//        int pos = getIndexOf(queryAddress, ":", 4);
//        String asPrefix = queryAddress.substring(0,pos);
//        String asAddress = asPrefix + "::1";
//        String aidStr = queryAddress.substring(pos+1);
//        String AID = aidStr.replace(":","");
//        String AIDnTH = userMapper.getAIDnTH(AID);
//        BigInteger big1 = new BigInteger(AID, 16);
//        BigInteger big2 = new BigInteger(AIDnTH, 16);
//        String timeHash = big1.xor(big2).toString(16);
//        String ideaKey = userMapper.getIdeaKey(asAddress, timeHash);
//        if (ideaKey == null)
//            throw new Exception("获取密钥出错！密钥集为空");
//
//        // step2. use suffix of IPv6-address to get the whole encrypt data(128-bits)
//        String prefix = userMapper.getPrefix(AIDnTH);
//        BigInteger big3 = new BigInteger(AIDnTH, 16);
//        BigInteger big4 = new BigInteger(prefix, 16);
//        String suffix = big3.xor(big4).toString(16);
//        String preAID = prefix + suffix;
//
//        // step3. use the proper key to decrypt the encrypt data(128-bits)
//        String rawAID = EncDecUtils.ideaDecrypt(preAID, ideaKey);
//        if (rawAID == null || rawAID.length() != 16)
//            throw new Exception("解密出错！");
//
//        // step4. use the NID to query user information the return the info(userID, phoneNumber, address-generate-time etc.) to user
//        String NID = rawAID.substring(0,10);
//        String timeInfoStr = ConvertUtils.hexStringToBinString(rawAID.substring(10));
//        int timeInfo = Integer.parseInt(timeInfoStr, 2) * stamp;
//        LocalDateTime localDateTime2 = LocalDateTime.of(LocalDate.now().getYear(), 1, 1, 0, 0, 0);
//        long baseTime = localDateTime2.toEpochSecond(ZoneOffset.of("+8"));
//        long registerTime = (baseTime + timeInfo);
//        Instant instant = Instant.ofEpochSecond(registerTime);
//        LocalDateTime registerTimeStr = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
//        QueryInfo queryInfo = userMapper.queryAddrInfo(NID);
//        queryInfo.setRegisterTime(registerTimeStr.toString());
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("userID", queryInfo.getUserID());
//        jsonObject.put("phoneNumber", queryInfo.getPhoneNumber());
//        jsonObject.put("registerTime", queryInfo.getRegisterTime());
//        jsonObject.put("userName", queryInfo.getUserName());
//        return jsonObject;
    }

    private int getIndexOf(String data, String str, int num) {
        Pattern pattern = Pattern.compile(str);
        Matcher matcher = pattern.matcher(data);

        int indexNum = 0;

        while (matcher.find()) {
            indexNum++;
            if (indexNum == num)
                break;
        }

        return matcher.start();
    }
}
