package com.hust.addrgeneration.serviceImpl;

import com.alibaba.fastjson.JSONObject;
import com.hust.addrgeneration.beans.*;
import com.hust.addrgeneration.dao.UserMapper;
import com.hust.addrgeneration.service.IPv6AddrService;
import com.hust.addrgeneration.utils.ConvertUtils;
import com.hust.addrgeneration.utils.EncDecUtils;
import com.hust.addrgeneration.utils.HashUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class IPv6AddrServiceImpl implements IPv6AddrService {
    private final UserMapper userMapper;
    private static final Logger logger = LoggerFactory.getLogger(IPv6AddrServiceImpl.class);

    @Autowired
    public IPv6AddrServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public ResponseEntity<UserResponse> registerNID(User infoBean) {
        UserResponse response = new UserResponse();

        User user = infoBean;
        String userID = user.getUserID();
        String password = user.getPassword();
        String phoneNumber = user.getPhoneNumber();
        String userName = user.getUsername();


        // step0. Check phoneNumber validation
        String phoneRegexp = "^((13[0-9])|(14[57])|(15[0-35-9])|(16[2567])|(17[0-8])|(18[0-9])|(19[0-9]))\\d{8}$";
        if(!Pattern.matches(phoneRegexp,phoneNumber)){
            response.setCode(10012);
            response.setMsg("手机号不合规");
            return new ResponseEntity<UserResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // step1. Calculate nid with user's information
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

        String nid = ConvertUtils.binStringToHexString(userPart + organizationPart);
        user.setNid(nid);
        try{
            userMapper.register(nid,password,userID,phoneNumber, userName);
        } catch (Exception e) {
            response.setCode(10002);
            response.setMsg("Register Failed");
            return new ResponseEntity<UserResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.setCode(0);
        response.setMsg("Success");
        response.setUser(user);
        return new ResponseEntity<UserResponse>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<AddressResponse> createAddr(Address addressInfo) throws Exception {
        AddressResponse response = new AddressResponse();

        String nid = addressInfo.getNid();
        String password = addressInfo.getPassword();
        String prefix = addressInfo.getPrefix();
        String suffix = addressInfo.getSuffix();
        if(prefix == null || prefix.isEmpty())
            prefix = "2001:0250";
        if(suffix == null || suffix.isEmpty())
            suffix = "1dd2:c65e:8f8b:95b2";
        logger.info(nid + password);
        logger.info(prefix + suffix);

        // step0. check if address is applied
        String address = userMapper.queryAIDTrunc(nid);
        if(address != null){
            response.setCode(10011);
            response.setMsg("地址已生成，请勿重复申请");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // step1. check nid and password
        /*
         if the nid isn't in the database, return the information tells user to register a nid
         if the nid isn't match the password, return the wrong password information
         */
        try{
            userMapper.queryRegisterInfo(nid);
        } catch (Exception e){
            response.setCode(10006);
            response.setMsg("缺少NID，请注册");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String passwordFromDB = userMapper.queryRegisterPassword(nid);
        if (!passwordFromDB.equals(password)) {
            response.setCode(10005);
            response.setMsg("密码错误");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // step2. Calculate the time information
        LocalDateTime localDateTime1 = LocalDateTime.now();
        LocalDateTime localDateTime2 = LocalDateTime.of(localDateTime1.getYear(), 1, 1, 0, 0, 0);

        long currentTime = localDateTime1.toEpochSecond(ZoneOffset.of("+8"));
        long baseTime = localDateTime2.toEpochSecond(ZoneOffset.of("+8"));

        int timeDifference = (int) (( currentTime - baseTime ) / 10);
        String timeInformation = ConvertUtils.decToBinString(timeDifference, 24);
        String rawAID = nid + ConvertUtils.binStringToHexString(timeInformation);

        // step3. Generate AID-noTimeHash(aka AID_nTH) with UID and time information
        String preAID = EncDecUtils.ideaEncrypt(rawAID, EncDecUtils.ideaKey);
        if(preAID == null || preAID.length() != 32){
            response.setCode(10009);
            response.setMsg("加密出错!");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info(preAID);
        String str1 = preAID.substring(0,16);
        String str2 = preAID.substring(16,32);

        BigInteger big1 = new BigInteger(str1, 16);
        BigInteger big2 = new BigInteger(str2, 16);
        String AIDnTH = big1.xor(big2).toString(16);

        try{
            userMapper.updateAIDnTH(AIDnTH, big1.toString(16));
        } catch (Exception e) {
            response.setCode(10003);
            response.setMsg("地址生成失败");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        // step4. Generate AID-withTimeHash(aka AID) with AIDnTH and time-Hash
        LocalDateTime localDateTime3 = LocalDateTime.of(localDateTime1.getYear(),localDateTime1.getMonth(),localDateTime1.getDayOfMonth(),localDateTime1.getHour(),0,0);
        long nearestTimeHour = localDateTime3.toEpochSecond(ZoneOffset.of("+8"));
        int timeDifference2 = (int) (nearestTimeHour - baseTime);
        String timeHash = EncDecUtils.md5Encrypt16(ConvertUtils.decToHexString(timeDifference2,10));

        BigInteger big3 = new BigInteger(AIDnTH,16);
        BigInteger big4 = new BigInteger(timeHash, 16);
        String AID = big3.xor(big4).toString(16);
        try{
            userMapper.updateAID(AID, AIDnTH);
        } catch (Exception e){
            response.setCode(10003);
            response.setMsg("地址生成失败");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // step5. Trunc AID with given prefix length and store to database
        int prefixLength = prefix.replace(":","").length();
        String visibleAID = AID.substring(0, 16 - prefixLength);
        String hiddenAID = AID.substring(16 - prefixLength);
        String prefix64bits = prefix.replace(":","") + visibleAID;
        StringBuilder prefix64 = new StringBuilder();
        for (int i = 0; i < prefix64bits.length(); i+=4) {
            prefix64.append(prefix64bits, i, i + 4).append(":");
        }
        String generateAddr = prefix64 + suffix;
        try{
            userMapper.updateAIDTrunc(generateAddr.replace(":",""), visibleAID, hiddenAID, timeDifference, nid, currentTime);
        } catch (Exception e){
            response.setCode(10003);
            response.setMsg("地址生成失败");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.setCode(0);
        response.setMsg("success");
        response.setAddress(generateAddr);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Override
    public ResponseEntity<QueryResponse> queryAddr(Query queryInfo) throws Exception {
        QueryResponse response = new QueryResponse();

        // step1. revert AID
        String queryAddress = queryInfo.getQueryAddress().replace(":","");
        int timeDifference = userMapper.queryAIDTruncTime(queryAddress);
        int prefixLength = queryInfo.getPrefix().replace(":","").length();
        String visibleAID = queryAddress.substring(prefixLength,16);
        String hiddenAID = userMapper.queryAIDTruncHiddenAID(visibleAID,timeDifference);
        String AID = visibleAID + hiddenAID;
        // step2. use prefix of the IPv6-address and calculate time-Hash to get key
        String asPrefix = "2001:250:4000:4507";
        String asAddress = asPrefix + "::1";
        String AIDnTH = userMapper.queryAIDAIDnTH(AID);
        BigInteger big1 = new BigInteger(AID, 16);
        BigInteger big2 = new BigInteger(AIDnTH, 16);
        String timeHash = big1.xor(big2).toString(16);

        // step3. use suffix of IPv6-address to get the whole encrypt data(128-bits)
        String prefix = userMapper.queryAIDnTHPrefix(AIDnTH);
        BigInteger big3 = new BigInteger(AIDnTH, 16);
        BigInteger big4 = new BigInteger(prefix, 16);
        String suffix = big3.xor(big4).toString(16);
        String preAID = prefix + suffix;

        // step4. use the proper key to decrypt the encrypt data(128-bits)
        String ideakey = userMapper.getIdeaKey(timeHash, asAddress);
        if (ideakey == null){
            response.setCode(10007);
            response.setMsg("获取密钥出错！密钥集为空");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        String rawAID = EncDecUtils.ideaDecrypt(preAID, ideakey);
        if (rawAID == null || rawAID.length() != 16) {
            response.setCode(10008);
            response.setMsg("解密出错!");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // step5. use the nid to query user information the return the info(userID, phoneNumber, address-generate-time etc.) to user
        String nid = rawAID.substring(0,10);
        String timeInfoStr = ConvertUtils.hexStringToBinString(rawAID.substring(10));
        int timeInfo = Integer.parseInt(timeInfoStr, 2) * 10;
        LocalDateTime localDateTime2 = LocalDateTime.of(LocalDate.now().getYear(), 1, 1, 0, 0, 0);
        long baseTime = localDateTime2.toEpochSecond(ZoneOffset.of("+8"));
        long registerTime = (baseTime + timeInfo);
        Instant instant = Instant.ofEpochSecond(registerTime);
        LocalDateTime registerTimeStr = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        User userInfo = userMapper.queryRegisterInfo(nid);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userID", userInfo.getUserID());
        jsonObject.put("phoneNumber", userInfo.getPhoneNumber());
        jsonObject.put("registerTime", registerTimeStr.toString());
        jsonObject.put("username", userInfo.getUsername());

        response.setCode(0);
        response.setMsg("success");
        response.setInfo(jsonObject);
        return new ResponseEntity<>(response, HttpStatus.OK);
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
