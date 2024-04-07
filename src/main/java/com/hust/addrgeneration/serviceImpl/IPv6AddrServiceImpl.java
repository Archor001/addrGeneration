package com.hust.addrgeneration.serviceImpl;

import com.hust.addrgeneration.beans.*;
import com.hust.addrgeneration.dao.UserMapper;
import com.hust.addrgeneration.service.IPv6AddrService;
import com.hust.addrgeneration.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class IPv6AddrServiceImpl implements IPv6AddrService {
    private final UserMapper userMapper;
    private static final Logger logger = LoggerFactory.getLogger(IPv6AddrServiceImpl.class);
    private ISP ispPrefix = ISPUtils.ispPrefix;
    private float syncGap = 24;

    @Autowired
    public IPv6AddrServiceImpl(UserMapper userMapper, Custom custom) {
        this.ispPrefix = new ISP(custom.getIsp(),custom.getIspLength());
        this.userMapper = userMapper;
    }

    // 创建用户(注册NID+地址生成)
    @Override
    public ResponseEntity<?> register(User infoBean) {
        GenerateAddressResponse response = new GenerateAddressResponse();

        String userID = infoBean.getUserID();
        String password = infoBean.getPassword();
        String phoneNumber = infoBean.getPhoneNumber();
        String username = infoBean.getUsername();

        // 第一步：判断平台是否创建了ISP前缀
        try {
            String prefix = ispPrefix.getIsp();
        } catch (Exception e) {
            return response.responseError(10018);
        }

        // 第二步: 检查手机号码是否合法
        String phoneRegexp = "^((13[0-9])|(14[57])|(15[0-35-9])|(16[2567])|(17[0-8])|(18[0-9])|(19[0-9]))\\d{8}$";
        if(!Pattern.matches(phoneRegexp,phoneNumber)){
            return response.responseError(10012);
        }

        // 第三步：判断是否可以创建用户
        User checkUser = userMapper.queryPhoneNumber(phoneNumber);
        if(checkUser != null && checkUser.getStatus() != 3){
            return response.responseError(10015);
        }

        String nid = generateNID(username, phoneNumber, userID);
        infoBean.setNid(nid);
        infoBean.setStatus(1);

        if(checkUser != null && checkUser.getStatus() == 3){    // 注册的是之前删除过的手机号
            try{
                userMapper.updateUser(infoBean);
            } catch (Exception e){
                return response.responseError(10002);
            }
        } else {        // 注册的是新用户
            try{
                userMapper.register(nid,password,userID,phoneNumber, username, 1);
            } catch (Exception e) {
                return response.responseError(10002);
            }
        }

        try{
            GenerateAddress addressInfo = new GenerateAddress();
            addressInfo.setPhoneNumber(infoBean.getPhoneNumber());
            addressInfo.setPassword(infoBean.getPassword());
            return this.createAddr(addressInfo);
        } catch (Exception e){
            return response.responseError(10003);
        }

    }

    // 修改用户 （重新生成NID+IPv6地址）
    @Override
    public ResponseEntity<?> updateUser(User user) throws Exception {
        GenerateAddressResponse response = new GenerateAddressResponse();

        int ispLength = ispPrefix.getLength();
        if(ispLength == 0){
            return response.responseError(10018);
        }

        String phoneNumber = user.getPhoneNumber();
        String username = user.getUsername();
        String password = user.getPassword();
        String userID = user.getUserID();

        String phoneRegexp = "^((13[0-9])|(14[57])|(15[0-35-9])|(16[2567])|(17[0-8])|(18[0-9])|(19[0-9]))\\d{8}$";
        if(!Pattern.matches(phoneRegexp,phoneNumber)){
            return response.responseError(10005);
        }

        String newNID = generateNID(username, phoneNumber, userID);
        if(password != null) user.setPassword(password);
        if(username != null) user.setUsername(username);
        if(userID != null) user.setUserID(userID);
        user.setNid(newNID);
        user.setStatus(1);

        try{
            userMapper.updateUser(user);
        } catch (Exception e){
            return response.responseError(10024);
        }

        try{
            GenerateAddress addressInfo = new GenerateAddress();
            addressInfo.setPhoneNumber(user.getPhoneNumber());
            addressInfo.setPassword(user.getPassword());
            return this.createAddr(addressInfo);
        } catch (Exception e){
            return response.responseError(10003);
        }
    }

    // 地址生成
    @Override
    public ResponseEntity<?> createAddr(GenerateAddress addressInfo) throws Exception {
        GenerateAddressResponse response = new GenerateAddressResponse();

        String phoneNumber = addressInfo.getPhoneNumber();
        String password = addressInfo.getPassword();
        String prefix = "";
        try {
            prefix = ispPrefix.getIsp();
        } catch (Exception e) {
            return response.responseError(10018);
        }

        User user = userMapper.queryPhoneNumber(phoneNumber);
        if(user==null){
            return response.responseError(10004);
        }

        String nid = user.getNid();

        // 如果没有ISP
        if(prefix == null || prefix.isEmpty()){
            return response.responseError(10018);
        }

        // step1. check nid and password
        /*
         if the nid isn't in the database, return the information tells user to register a nid
         if the nid isn't match the password, return the wrong password information
         */

        if (!user.getPassword().equals(password)) {
            return response.responseError(10005);
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
            return response.responseError(10009);
        }
        String str1 = preAID.substring(0,16);
        String str2 = preAID.substring(16,32);

        BigInteger big1 = new BigInteger(str1, 16);
        BigInteger big2 = new BigInteger(str2, 16);
        String AIDnTH = String.format("%016x", big1.xor(big2));

        // step4. Generate AID-withTimeHash(aka AID) with AIDnTH and time-Hash
        LocalDateTime localDateTime3 = LocalDateTime.of(localDateTime1.getYear(),localDateTime1.getMonth(),localDateTime1.getDayOfMonth(),localDateTime1.getHour(),0,0);
        long nearestTimeHour = localDateTime3.toEpochSecond(ZoneOffset.of("+8"));
        int timeDifference2 = (int) (nearestTimeHour - baseTime);
        String timeHash = EncDecUtils.md5Encrypt16(ConvertUtils.decToHexString(timeDifference2,10));

        BigInteger big3 = new BigInteger(AIDnTH,16);
        BigInteger big4 = new BigInteger(timeHash, 16);
        String AID = String.format("%016x", big3.xor(big4));
        try{
            userMapper.updateAID(AID, AIDnTH, String.format("%016x", big1));
        } catch (Exception e){
            return response.responseError(10003);
        }

        // step5. Trunc AID with given prefix length and store to database
        int prefixLength = ispPrefix.getLength() / 4;
        String visibleAID = AID.substring(0, 16 - prefixLength);
        String hiddenAID = AID.substring(16 - prefixLength);
        String prefix64bits = AddressUtils.parseAddressToString(prefix, prefixLength) + visibleAID;
        StringBuilder prefix64 = new StringBuilder();
        for (int i = 0; i < prefix64bits.length(); i+=4) {
            prefix64.append(prefix64bits, i, i + 4).append(":");
        }
        prefix64.deleteCharAt(prefix64.length() - 1);
        String generateAddr = String.valueOf(prefix64);
        try{
            userMapper.updateAIDTrunc(AddressUtils.displayAddress(generateAddr),generateAddr.replace(":",""), visibleAID, hiddenAID, timeDifference, phoneNumber, currentTime, prefix, 1);
        } catch (Exception e){
            return response.responseError(10003);
        }

        UserAddress userAddress = new UserAddress();
        userAddress.setNid(user.getNid());
        userAddress.setAddress(generateAddr);
        userAddress.setPrefix(prefix + "::/" + ispPrefix.getLength());
        userAddress.setRegisterTime(currentTime);

        response.setCode(0);
        response.setMsg("success");
        response.setUser(userAddress);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 地址查询
    public ResponseEntity<?> queryAddr(String phoneNumber) throws Exception {
        QueryAddressResponse response = new QueryAddressResponse();

        int ispLength = ispPrefix.getLength();
        if(ispLength == 0){
            return response.responseError(10018);
        }

        List<String> rntAddressList = new ArrayList<>();

        if(phoneNumber.contains(",")){
            String[] phones = phoneNumber.split(",");
            for(String phone: phones){
                List<Address> addressList;
                try{
                    addressList = userMapper.queryAIDTruncAddress(phone);
                } catch (Exception e){
                    return response.responseError(10023);
                }
                if(addressList.isEmpty()){
                    return response.responseError(10022);
                }
                String[] addressArray = addressList.stream().map(Address::getAddress).toArray(String[]::new);
                String addressStr = String.join(",", addressArray);
                rntAddressList.add(addressStr);
            }
        } else {
            List<Address> addressList;
            try{
                addressList = userMapper.queryAIDTruncAddress(phoneNumber);
            } catch (Exception e){
                return response.responseError(10023);
            }
            if(addressList.isEmpty()){
                return response.responseError(10022);
            }
            String[] addressArray = addressList.stream().map(Address::getAddress).toArray(String[]::new);
            String addressStr = String.join(",", addressArray);
            rntAddressList.add(addressStr);
        }

        String[] rntAddress = rntAddressList.stream().toArray(String[]::new);

        response.setCode(0);
        response.setMsg("success");
        response.setAddress(rntAddress);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 地址溯源
    @Override
    public ResponseEntity<?> traceAddr(String queryAddress) throws Exception {
        TraceAddressResponse response = new TraceAddressResponse();
        // step1. revert AID
        int fourthColonIndex = queryAddress.indexOf(':',queryAddress.indexOf(':', queryAddress.indexOf(':', queryAddress.indexOf(':') + 1) + 1) + 1);
        // If the fourth colon is found, modify queryAddress
        if (fourthColonIndex != -1) {
            queryAddress = queryAddress.substring(0, fourthColonIndex);
        }
        String addressAID = AddressUtils.parseAddressToString(queryAddress, 16);
        QueryAIDTrunc queryAIDResult;
        try{
            queryAIDResult = userMapper.queryAIDTruncResult(addressAID);
        } catch (Exception e) {
            return response.responseError(10016);
        }
        int prefixLength = ispPrefix.getLength() / 4;
        if(prefixLength == 0){
            return response.responseError(10018);
        }
        String visibleAID = addressAID.substring(prefixLength,16);
        String hiddenAID = userMapper.queryAIDTruncHiddenAID(visibleAID, queryAIDResult.getTimeDifference());
        String AID = visibleAID + hiddenAID;
        // step2. use prefix of the IPv6-address and calculate time-Hash to get key
        String asPrefix = "2001:250:4000:4507";
        String asAddress = asPrefix + "::1";
        String AIDnTH = userMapper.queryAIDnTH(AID);
        BigInteger big1 = new BigInteger(AID, 16);
        BigInteger big2 = new BigInteger(AIDnTH, 16);
        String timeHash = String.format("%016x", big1.xor(big2));

        // step3. use suffix of IPv6-address to get the whole encrypt data(128-bits)
        String prefix = userMapper.queryPrefix(AIDnTH);
        BigInteger big3 = new BigInteger(AIDnTH, 16);
        BigInteger big4 = new BigInteger(prefix, 16);
        String suffix = String.format("%016x", big3.xor(big4));
        String preAID = prefix + suffix;

        // step4. use the proper key to decrypt the encrypt data(128-bits)
        String ideakey = userMapper.getIdeaKey(timeHash, asAddress);
        if (ideakey == null){
            return response.responseError(10007);
        }
        String rawAID = EncDecUtils.ideaDecrypt(preAID, ideakey);
        if (rawAID == null || rawAID.length() != 16) {
            return response.responseError(10008);
        }

        // step5. use the nid to query user information the return the info(userID, phoneNumber, address-generate-time etc.) to user
        String nid = rawAID.substring(0,10);
        String timeInfoStr = ConvertUtils.hexStringToBinString(rawAID.substring(10));
        int timeInfo = Integer.parseInt(timeInfoStr, 2) * 10;
        LocalDateTime localDateTime2 = LocalDateTime.of(LocalDate.now().getYear(), 1, 1, 0, 0, 0);
        long baseTime = localDateTime2.toEpochSecond(ZoneOffset.of("+8"));
        long registerTime = (baseTime + timeInfo);

        User userInfo = userMapper.queryRegisterInfo(nid);
        UserTrace userTrace = new UserTrace();
        userTrace.setRegisterTime(registerTime);
        userTrace.setAddressStatus(queryAIDResult.getStatus());
        userTrace.setUserID(userInfo.getUserID());
        userTrace.setNid(userInfo.getNid());
        userTrace.setPhoneNumber(userInfo.getPhoneNumber());
        userTrace.setStatus(userInfo.getStatus());
        userTrace.setUsername(userInfo.getUsername());

        response.setCode(0);
        response.setMsg("success");
        response.setUser(userTrace);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 批量获取地址
    @Override
    public ResponseEntity<?> filterAddress(int offset, int limit, String content) throws Exception {
        AddressManageResponse response = new AddressManageResponse();

        int ispLength = ispPrefix.getLength();
        if(ispLength == 0){
            return response.responseError(10018);
        }

        List<Address> addressList = new ArrayList<>();
        if(limit == 0) limit = 1000;
        try{
            addressList = userMapper.getAddressesByFilter(offset, limit, content);
        } catch (Exception e){
            return response.responseError(10021);
        }

        int addressCount = 0;
        try{
            addressCount = userMapper.getAddressCountByFilter(content);
        } catch (Exception e){
            return response.responseError(10021);
        }

        response.setCode(0);
        response.setMsg("success");
        response.setAddresses(addressList.toArray(new Address[addressList.size()]));
        response.setCount(addressCount);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 修改ISP(更新ISP+重新生成地址)
    @Override
    public ResponseEntity<?> updateISP(ISP isp) throws Exception{
        Response response = new Response();
        String ispStr = isp.getIsp();
        int length = 0;
        if(ispStr.contains("::/")) {
            int pos = ispStr.indexOf("::/");
            try{
                length = Integer.parseInt(ispStr.substring(pos+3), 10);
                ispStr = ispStr.substring(0, pos);
            } catch (Exception e) {
                return response.responseError(10019);
            }
        } else {
            length = AddressUtils.getAddressBitLength(ispStr);
        }

        if(ispPrefix.getIsp() != ispStr || ispPrefix.getLength() != length){
            ispPrefix.setIsp(ispStr);
            ispPrefix.setLength(length);
            // 如果ISP发生了变化，重新生成地址
            // this.regenAddress();
        }
        response.setCode(0);
        response.setMsg("success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 获取ISP地址
    @Override
    public ResponseEntity<?> getISP() throws Exception{
        ISPResponse response = new ISPResponse();
        String isp = ispPrefix.getIsp();
        int length = ispPrefix.getLength();
        if(isp==null||isp.isEmpty()||length == 0){
            return response.responseError(10018);
        }
        response.setIsp(ispPrefix.getIsp());
        response.setLength(ispPrefix.getLength());
        response.setCode(0);
        response.setMsg("success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 重新生成地址
    @Override
    public ResponseEntity<?> regenAddress() throws Exception {
        Response response = new Response();

        // 截断地址表(deprecated)
//        try{
//            userMapper.truncateAIDTrunc();
//        } catch (Exception e){
//            return response.responseError(10020);
//        }

        // 停用地址
        try{
            userMapper.suspendAIDTrunc();
        } catch (Exception e){
            return response.responseError(10020);
        }

        // 获取全部register表信息，逐个生成地址
        List<User> userList = new ArrayList<User>();
        try{
            userList = userMapper.getAllRegisteredUsers();
        } catch (Exception e){
            return response.responseError(10020);
        }
        User[] users = userList.toArray(new User[userList.size()]);
        for(User i : users){
            if(i.getStatus() == 3)
                continue;
            try{
                GenerateAddress addressInfo = new GenerateAddress();
                addressInfo.setPhoneNumber(i.getPhoneNumber());
                addressInfo.setPassword(i.getPassword());
                this.createAddr(addressInfo);
            } catch (Exception e){
                response.responseError(10020);
            }
        }

        response.setCode(0);
        response.setMsg("success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 获取系统配置
    @Override
    public ResponseEntity<?> getConfig() throws Exception {
        SystemResponse response = new SystemResponse();

        String isp = ispPrefix.getIsp();
        int length = ispPrefix.getLength();
        if(isp==null||isp.isEmpty()||length == 0){
            return response.responseError(10018);
        }

        response.setIsp(ispPrefix);
        response.setSyncGap(syncGap);
        response.setCode(0);
        response.setMsg("success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> setSyncGap(float gap) throws Exception {
        Response response = new Response();
        syncGap = gap;
        response.setCode(0);
        response.setMsg("success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private String generateNID(String username, String phoneNumber, String userID){
        String encryptStr = userID + phoneNumber + username;
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

        return ConvertUtils.binStringToHexString(userPart + organizationPart);
    }
}
