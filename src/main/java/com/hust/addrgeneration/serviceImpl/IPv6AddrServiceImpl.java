package com.hust.addrgeneration.serviceImpl;

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
    private int stamp = 10;
    private static final Logger logger = LoggerFactory.getLogger(IPv6AddrServiceImpl.class);

    @Autowired
    public IPv6AddrServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    // 创建用户
    @Override
    public ResponseEntity<UserResponse> createUser(InfoBean infoBean) throws Exception {
        UserResponse response = new UserResponse();
        String name = infoBean.getName();
        String password = infoBean.getPassword();
        String phoneNumber = infoBean.getPhoneNumber();
        String username = infoBean.getUsername();
        String emailAddress = infoBean.getEmailAddress();
        int role = infoBean.getRole();

        // Step1. 检查参数合法性
        String phoneRegexp = "^((13[0-9])|(14[57])|(15[0-35-9])|(16[2567])|(17[0-8])|(18[0-9])|(19[0-9]))\\d{8}$";
        if(!Pattern.matches(phoneRegexp,phoneNumber)){
            return response.responseError(10005);
        }
        if( role<0 || role>4 ){
            return response.responseError(10006);
        }
        String emailRegexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";
        if(!Pattern.matches(emailRegexp,emailAddress)){
            return response.responseError(10007);
        }

        // Step2. 生成NID
        String nid = generateNID(username, phoneNumber, name);

        // Step3. 写入数据库
        try {
            userMapper.register(nid, password, username, phoneNumber, name, emailAddress, role);
        } catch (Exception e) {
            return response.responseError(10003);
        }
        response.setNid(nid);
        response.setCode(0);
        response.setMsg("success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 删除用户
    @Override
    public ResponseEntity<Response> deleteUser(String userContent) throws  Exception {
        Response response = new Response();
        if(userContent.contains(",")){  // 批量
            String[] users = userContent.split(",");
            for(String user : users){
                try {
                    userMapper.deleteUser(user);
                } catch (Exception e){
                    return response.responseNormalError(10004);
                }
            }
        } else {
            try {
                userMapper.deleteUser(userContent);
            } catch (Exception e){
                return response.responseNormalError(10004);
            }
        }
        response.setMsg("success");
        response.setCode(0);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 修改用户
    @Override
    public ResponseEntity<UserResponse> updateUser(InfoBean infoBean) throws  Exception {
        UserResponse response = new UserResponse();

        // Step1. 检查nid是否存在
        String nid = infoBean.getNid();
        if(nid == null || nid.length() <= 0){
            return response.responseError(10009);
        }
        InfoBean user = new InfoBean();
        try{
            user = userMapper.getUser(nid);
        } catch (Exception e){
            return response.responseError(10008);
        }

        // Step2. 检查参数合法性
        String username = user.getUsername();
        String name = infoBean.getName();
        String phoneNumber = infoBean.getPhoneNumber();
        String emailAddress = infoBean.getEmailAddress();
        int role = infoBean.getRole();

        String phoneRegexp = "^((13[0-9])|(14[57])|(15[0-35-9])|(16[2567])|(17[0-8])|(18[0-9])|(19[0-9]))\\d{8}$";
        if(!Pattern.matches(phoneRegexp,phoneNumber)){
            return response.responseError(10005);
        }
        if( role<0 || role>4 ){
            return response.responseError(10006);
        }
        String emailRegexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";
        if(!Pattern.matches(emailRegexp,emailAddress)){
            return response.responseError(10007);
        }

        // Step3. 重新生成NID
        String newNID = generateNID(username,phoneNumber,name);
        infoBean.setUsername(username);
        infoBean.setNid(newNID);

        // Step4. 数据库更新
        try{
            userMapper.updateUser(infoBean);
        } catch (Exception e){
            return response.responseError(10008);
        }
        response.setCode(0);
        response.setMsg("success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 地址生成
    @Override
    public ResponseEntity<GenerateAddressResponse> generateAddr(GenerateAddress generateAddress) throws Exception {
        GenerateAddressResponse response = new GenerateAddressResponse();

        String nid = generateAddress.getNid();
        String password = generateAddress.getPassword();
        // step1. check nid and password
        /*
         if the nid isn't in the database, return the information tells user to register a nid
         if the nid isn't match the password, return the wrong password information
         */
        String passwordFromDB = userMapper.getPasswordFromNID(nid);
        if (passwordFromDB == null || passwordFromDB.length() <= 0){
            return response.responseError(10009);
        }
        if (!passwordFromDB.equals(password)) {
            return response.responseError(10002);
        }

        // step2. Calculate the time information
        LocalDateTime localDateTime1 = LocalDateTime.now();
        LocalDateTime localDateTime2 = LocalDateTime.of(localDateTime1.getYear(), 1, 1, 0, 0, 0);

        long currentTime = localDateTime1.toEpochSecond(ZoneOffset.of("+8"));
        long baseTime = localDateTime2.toEpochSecond(ZoneOffset.of("+8"));

        int timeDifference = (int) (( currentTime - baseTime ) / stamp);
        String timeInformation = ConvertUtils.decToBinString(timeDifference, 24);
        String rawAID = nid + ConvertUtils.binStringToHexString(timeInformation);

        // step3. Generate AID-noTimeHash(aka AID_nTH) with UID and time information
        String preAID = EncDecUtils.ideaEncrypt(rawAID, EncDecUtils.ideaKey);
        String str1 = preAID.substring(0,16);
        String str2 = preAID.substring(16,32);

        BigInteger big1 = new BigInteger(str1, 16);
        BigInteger big2 = new BigInteger(str2, 16);
        String AIDnTH = big1.xor(big2).toString(16);

        String prefix = userMapper.getAIDPrefix(AIDnTH);
        if(prefix != null){
            return response.responseError(10010);
        }
        try {
            userMapper.updateAID(AIDnTH, big1.toString(16));
        } catch (Exception e){
            return response.responseError(10011);
        }

        // step4. Generate AID-withTimeHash(aka AID) with AIDnTH and time-Hash
        LocalDateTime localDateTime3 = LocalDateTime.of(localDateTime1.getYear(),localDateTime1.getMonth(),localDateTime1.getDayOfMonth(),localDateTime1.getHour(),0,0);
        long nearestTimeHour = localDateTime3.toEpochSecond(ZoneOffset.of("+8"));
        int timeDifference2 = (int) (nearestTimeHour - baseTime);
        String timeHash = EncDecUtils.md5Encrypt16(ConvertUtils.decToHexString(timeDifference2,10));

        BigInteger big3 = new BigInteger(AIDnTH,16);
        BigInteger big4 = new BigInteger(timeHash, 16);
        String AID = String.format("%016x", big3.xor(big4));
        try{
            userMapper.updateTimeHash(AID, AIDnTH);
        } catch (Exception e){
            return response.responseError(10011);
        }

        StringBuilder suffix = new StringBuilder();
        for (int i = 0; i < AID.length(); i+=4) {
            suffix.append(AID, i, i + 4).append(":");
        }
        String address = "2001:250:4000:4507:" + suffix.substring(0,suffix.length()-1);

        response.setCode(0);
        response.setMsg("success");
        response.setAddress(address);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<QueryAddressResponse> queryAddress(String nid) throws Exception {
        QueryAddressResponse response = new QueryAddressResponse();

        response.setCode(0);
        response.setMsg("success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 地址溯源
    @Override
    public ResponseEntity<TraceAddressResponse> traceAddress(String queryAddress) throws Exception {
        TraceAddressResponse response = new TraceAddressResponse();

        // step1. use prefix of the IPv6-address and calculate time-Hash to get key
        int pos = getIndexOf(queryAddress, ":", 4);
        String asPrefix = queryAddress.substring(0,pos);
        String asAddress = asPrefix + "::1";
        String aidStr = queryAddress.substring(pos+1);
        String AID = aidStr.replace(":","");
        String AIDnTH = "";
        try {
            AIDnTH = userMapper.getAIDnTH(AID);
        } catch (Exception e){
            return response.responseError(10012);
        }
        BigInteger big1 = new BigInteger(AID, 16);
        BigInteger big2 = new BigInteger(AIDnTH, 16);
        String timeHash = big1.xor(big2).toString(16);
        String ideaKey = userMapper.getIdeaKey(asAddress, timeHash);
        if (ideaKey == null) {
            return response.responseError(10013);
        }

        // step2. use suffix of IPv6-address to get the whole encrypt data(128-bits)
        String prefix = "";
        try{
            prefix = userMapper.getAIDPrefix(AIDnTH);
        } catch (Exception e){
            return response.responseError(10012);
        }
        BigInteger big3 = new BigInteger(AIDnTH, 16);
        BigInteger big4 = new BigInteger(prefix, 16);
        String suffix = big3.xor(big4).toString(16);
        String preAID = prefix + suffix;

        // step3. use the proper key to decrypt the encrypt data(128-bits)
        String rawAID = EncDecUtils.ideaDecrypt(preAID, ideaKey);
        if (rawAID == null || rawAID.length() != 16) {
            return response.responseError(10014);
        }

        // step4. use the nid to query user information the return the info(userID, phoneNumber, address-generate-time etc.) to user
        String nid = rawAID.substring(0,10);
        String timeInfoStr = ConvertUtils.hexStringToBinString(rawAID.substring(10));
        int timeInfo = Integer.parseInt(timeInfoStr, 2) * stamp;
        LocalDateTime localDateTime2 = LocalDateTime.of(LocalDate.now().getYear(), 1, 1, 0, 0, 0);
        long baseTime = localDateTime2.toEpochSecond(ZoneOffset.of("+8"));
        long registerTime = (baseTime + timeInfo);
        Instant instant = Instant.ofEpochSecond(registerTime);
        LocalDateTime registerTimeStr = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        InfoBean user = userMapper.getUser(nid);
        if(user == null){
            return response.responseError(10012);
        }

        response.setCode(0);
        response.setMsg("success");
        response.setRegisterTime(registerTimeStr.toString());
        response.setInfoBean(user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 地址删除
    @Override
    public ResponseEntity<Response> deleteAddress(String address) throws Exception {
        Response response = new Response();

        response.setCode(0);
        response.setMsg("success");
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

    private String generateNID(String username, String phoneNumber, String name){
        String encryptStr = username + phoneNumber + name;
        String hashStr = HashUtils.SM3Hash(encryptStr);
        String userPart = ConvertUtils.hexStringToBinString(hashStr).substring(0,38);
        String userType = username.substring(0,1);
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
        return ConvertUtils.binStringToHexString(userPart + organizationPart);
    }
}
