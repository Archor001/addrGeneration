package com.hust.addrgeneration.serviceImpl;

import com.alibaba.fastjson.JSONObject;
import com.hust.addrgeneration.beans.InfoBean;
import com.hust.addrgeneration.beans.QueryInfo;
import com.hust.addrgeneration.dao.UserMapper;
import com.hust.addrgeneration.encrypt.IDEAUtils;
import com.hust.addrgeneration.service.IPv6AddrService;
import com.hust.addrgeneration.utils.ConvertUtils;
import com.hust.addrgeneration.utils.EncDecUtils;
import com.hust.addrgeneration.utils.HashUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.time.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class IPv6AddrServiceImpl implements IPv6AddrService {
    private final UserMapper userMapper;
    private int portCount = 0;
    private static final Logger logger = LoggerFactory.getLogger(IPv6AddrServiceImpl.class);

    @Autowired
    public IPv6AddrServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public String getNID(InfoBean infoBean) {
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
        userMapper.register(NID,password,userID,phoneNumber, userName);

        return NID;

    }

    @Override
    public String getAddr(InfoBean infoBean) throws Exception {
        String NID = infoBean.getNid();
        String password = infoBean.getPassword();
        logger.info(NID + password);
        // step1. check NID and password
        /*
         if the NID isn't in the database, return the information tells user to register a NID
         if the NID isn't match the password, return the wrong password information
         */
         String passwordFromDB = userMapper.getKey(NID);
        if (!passwordFromDB.equals(password)) {
            throw new Exception("密码错误，请重新输入！");
        }

        // step2. Calculate the time information
        LocalDateTime localDateTime1 = LocalDateTime.now();
        LocalDateTime localDateTime2 = LocalDateTime.of(localDateTime1.getYear(), 1, 1, 0, 0, 0);

        long currentTime = localDateTime1.toEpochSecond(ZoneOffset.of("+8"));
        long baseTime = localDateTime2.toEpochSecond(ZoneOffset.of("+8"));

        int timeDifference = (int) (( currentTime - baseTime ) / 10);
        String timeInformation = ConvertUtils.decToBinString(timeDifference, 24);
        String rawAID = NID + ConvertUtils.binStringToHexString(timeInformation);

        // step3. Generate AID-noTimeHash(aka AID_nTH) with UID and time information
        String preAID = EncDecUtils.ideaEncrypt(rawAID, EncDecUtils.ideaKey);
        String str1 = preAID.substring(0,16);
        String str2 = preAID.substring(16,32);

        BigInteger big1 = new BigInteger(str1, 16);
        BigInteger big2 = new BigInteger(str2, 16);
        String AIDnTH = big1.xor(big2).toString(16);

        userMapper.updateAID(AIDnTH, big1.toString(16));

        // step4. Generate AID-withTimeHash(aka AID) with AIDnTH and time-Hash
        LocalDateTime localDateTime3 = LocalDateTime.of(localDateTime1.getYear(),localDateTime1.getMonth(),localDateTime1.getDayOfMonth(),localDateTime1.getHour(),0,0);
        long nearestTimeHour = localDateTime3.toEpochSecond(ZoneOffset.of("+8"));
        int timeDifference2 = (int) (nearestTimeHour - baseTime);
        String timeHash = EncDecUtils.md5Encrypt16(ConvertUtils.decToHexString(timeDifference2,10));

        BigInteger big3 = new BigInteger(AIDnTH,16);
        BigInteger big4 = new BigInteger(timeHash, 16);
        String AID = big3.xor(big4).toString(16);
        userMapper.updateTimeHash(AID, AIDnTH);

        StringBuilder suffix = new StringBuilder();
        for (int i = 0; i < AID.length(); i+=4) {
            suffix.append(AID, i, i + 4).append(":");
        }
        String asPrefix = "2001:250:4000:4507:";
        return asPrefix + suffix.substring(0,suffix.length()-1);
    }

    @Override
    public String getSubnet(InfoBean infoBean) throws Exception {
        return null;
    }

    @Override
    public String creatPortWithIPv6Addr(InfoBean userInfo) throws Exception {
        JSONObject request = new JSONObject();
        String jsonString = "{\n" +
                "\t\"port\": {\n" +
                "\t\t\"admin_state_up\": true,\n" +
                "\t\t\"name\": \"true-port-" + portCount +"\",\n" +
                "\t\t\"tenant_id\": \"1c211c79a8c5437aa478479d1476bfac\",\n" +
                "\t\t\"network_id\": \"6b9fcbdd-e544-4095-9ac9-efe0f7bab51e\",\n" +
                "\t\t\"port_security_enabled\": true,\n" +
                "\t\t\"propagate_uplink_status\": false,\n" +
                "\t\t\"fixed_ips\" : [\n" +
                "\t\t    {\n" +
                "\t\t        \"nid\":\"" + userInfo.getNid() + "\",\n" +
                "\t\t        \"passwd\":\"" + userInfo.getPassword() + "\",\n" +
                "\t\t        \"subnet_id\" : \"255b0255-ba6a-4820-bf1c-0f5309b9c676\"\n" +
                "\t\t    }\n" +
                "\t\t]\n" +
                "\t}\n" +
                "}";
        request = JSONObject.parseObject(jsonString);
        JSONObject reply = getNormalResponse("http://192.168.248.143:9696/v2.0/ports", request);
        portCount++;
        return reply.getJSONObject("port").getJSONArray("fixed_ips").getJSONObject(0).getString("ip_address");
    }

    public static JSONObject getNormalResponse(String url, JSONObject json) throws Exception {
        return SendPostPacket(url, json);
    }

    private static JSONObject SendPostPacket(String url, JSONObject json) throws Exception {
        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpMethod method = HttpMethod.POST;
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");

        
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        headers.add("X-Auth-Token", "gAAAAABkj-zN619wuAG7vNbKubKOoceAhwRYX4ppd2PltQTYrTfhSXpZTwwS3Z-F40K_xRXEBkoHISBrQSNIGsHCGiC9TJazpJ7qSW0eciEhAWHcBeEdWF0Gn5m9nQd2ddwWtL9r8dqpYVedjsI8SgCuNc_n6p56l8FZ9LNfWdFEeYn6_XKNfmw");

        HttpEntity<String> requestEntity = new HttpEntity<String>(json.toString(), headers);

        String res = client.exchange(url, method, requestEntity, String.class).getBody();
        return JSONObject.parseObject(res);
    }

    @Override
    public JSONObject queryAddr(InfoBean infoBean) throws Exception {
        // step1. use prefix of the IPv6-address and calculate time-Hash to get key
        String queryAddress = infoBean.getQueryAddress();
        int pos = getIndexOf(queryAddress, ":", 4);
        String asPrefix = queryAddress.substring(0,pos);
        String asAddress = asPrefix + "::1";
        String aidStr = queryAddress.substring(pos+1);
        String AID = aidStr.replace(":","");
        String AIDnTH = userMapper.getAIDnTH(AID);
        BigInteger big1 = new BigInteger(AID, 16);
        BigInteger big2 = new BigInteger(AIDnTH, 16);
        String timeHash = big1.xor(big2).toString(16);
        String ideaKey = userMapper.getIdeaKey(asAddress, timeHash);
        if (ideaKey == null)
            throw new Exception("获取密钥出错！密钥集为空");

        // step2. use suffix of IPv6-address to get the whole encrypt data(128-bits)
        String prefix = userMapper.getPrefix(AIDnTH);
        BigInteger big3 = new BigInteger(AIDnTH, 16);
        BigInteger big4 = new BigInteger(prefix, 16);
        String suffix = big3.xor(big4).toString(16);
        String preAID = prefix + suffix;

        // step3. use the proper key to decrypt the encrypt data(128-bits)
        String rawAID = EncDecUtils.ideaDecrypt(preAID, ideaKey);
        if (rawAID == null || rawAID.length() != 16)
            throw new Exception("解密出错！");

        // step4. use the NID to query user information the return the info(userID, phoneNumber, address-generate-time etc.) to user
        String NID = rawAID.substring(0,10);
        String timeInfoStr = ConvertUtils.hexStringToBinString(rawAID.substring(10));
        int timeInfo = Integer.parseInt(timeInfoStr, 2) * 10;
        LocalDateTime localDateTime2 = LocalDateTime.of(LocalDate.now().getYear(), 1, 1, 0, 0, 0);
        long baseTime = localDateTime2.toEpochSecond(ZoneOffset.of("+8"));
        long registerTime = (baseTime + timeInfo);
        Instant instant = Instant.ofEpochSecond(registerTime);
        LocalDateTime registerTimeStr = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        QueryInfo queryInfo = userMapper.queryAddrInfo(NID);
        queryInfo.setRegisterTime(registerTimeStr.toString());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userID", queryInfo.getUserID());
        jsonObject.put("phoneNumber", queryInfo.getPhoneNumber());
        jsonObject.put("registerTime", queryInfo.getRegisterTime());
        jsonObject.put("userName", queryInfo.getUserName());
        return jsonObject;
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
