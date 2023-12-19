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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.hust.addrgeneration.utils.AddressUtils.parseAddressSuffix;

@Service
public class IPv6AddrServiceImpl implements IPv6AddrService {
    private final UserMapper userMapper;
    private int stamp = 10;
    private static final Logger logger = LoggerFactory.getLogger(IPv6AddrServiceImpl.class);

    @Autowired
    public IPv6AddrServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    // 批量地址查询
    @Override
    public ResponseEntity<?> filterAddress(int offset, int limit, String content) throws Exception {
        AddressManageResponse response = new AddressManageResponse();

        List<Address> addressList = new ArrayList<>();
        try{
            addressList = userMapper.getAddressesByFilter(offset, limit ,content);
        } catch (Exception e) {
            return response.responseError(10019);
        }

        int addressCount = 0;
        try{
            addressCount = userMapper.getAddressCountByFilter(content);
        } catch (Exception e){
            return response.responseError(10019);
        }

        response.setCode(0);
        response.setMsg("success");
        response.setAddresses(addressList.toArray(new Address[addressList.size()]));
        response.setCount(addressCount);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 地址生成
    @Override
    public ResponseEntity<?> generateAddress(GenerateAddress generateAddress) throws Exception {
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

        // Step2. Check if the user is suspended
        User user;
        try{
            user = userMapper.getUser(nid);
            if(user.getStatus() == 2){
                return response.responseError(10024);
            }
        } catch (Exception e){
            return response.responseError(10011);
        }

        // step3. Calculate the time information
        LocalDateTime localDateTime1 = LocalDateTime.now();
        LocalDateTime localDateTime2 = LocalDateTime.of(localDateTime1.getYear(), 1, 1, 0, 0, 0);

        long registerTime = localDateTime1.toEpochSecond(ZoneOffset.of("+8"));
        long baseTime = localDateTime2.toEpochSecond(ZoneOffset.of("+8"));

        int timeDifference = (int) (( registerTime - baseTime ) / stamp);
        String timeInformation = ConvertUtils.decToBinString(timeDifference, 24);
        String rawAID = nid + ConvertUtils.binStringToHexString(timeInformation);

        // step4. Generate AID-noTimeHash(aka AID_nTH) with UID and time information
        String preAID = EncDecUtils.ideaEncrypt(rawAID, EncDecUtils.ideaKey);
        String str1 = preAID.substring(0,16);
        String str2 = preAID.substring(16,32);

        BigInteger big1 = new BigInteger(str1, 16);
        BigInteger big2 = new BigInteger(str2, 16);
        String AIDnTH = String.format("%016x", big1.xor(big2));

        String prefix = userMapper.getAIDnTHPrefix(AIDnTH);
        if(prefix != null){
            return response.responseError(10010);
        }

        // step5. Generate AID-withTimeHash(aka AID) with AIDnTH and time-Hash
        LocalDateTime localDateTime3 = LocalDateTime.of(localDateTime1.getYear(),localDateTime1.getMonth(),localDateTime1.getDayOfMonth(),localDateTime1.getHour(),0,0);
        long nearestTimeHour = localDateTime3.toEpochSecond(ZoneOffset.of("+8"));
        int timeDifference2 = (int) (nearestTimeHour - baseTime);
        String timeHash = EncDecUtils.md5Encrypt16(ConvertUtils.decToHexString(timeDifference2,10));

        BigInteger big3 = new BigInteger(AIDnTH,16);
        BigInteger big4 = new BigInteger(timeHash, 16);
        String AID = String.format("%016x", big3.xor(big4));
        StringBuilder suffix = new StringBuilder();
        for (int i = 0; i < AID.length(); i+=4) {
            suffix.append(AID, i, i + 4).append(":");
        }
        String address = "2001:253:219:2222:" + suffix.substring(0,suffix.length()-1);

        try{
            userMapper.updateAID(AIDnTH, big1.toString(16), AID, nid, registerTime, address, 1);
        } catch (Exception e){
            return response.responseError(10011);
        }

        response.setCode(0);
        response.setMsg("success");
        response.setAddress(address);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 地址查询
    @Override
    public ResponseEntity<?> queryAddress(String nid) throws Exception {
        QueryAddressResponse response = new QueryAddressResponse();

        // 检查用户是否停用
        try {
            User user = userMapper.getUser(nid);
            if (user.getStatus() == 2) {
                return response.responseError(10024);
            }
        } catch (Exception e){
            return response.responseError(10015);
        }

        List<Address> address;
        try{
            address = userMapper.getAddress(nid);
        } catch (Exception e){
            return response.responseError(10015);
        }
        if(address.isEmpty()){
            return response.responseError(10020);
        }

        String[] addressArray = address.stream().map(Address::getAddress).toArray(String[]::new);
        String addressStr = String.join(",", addressArray);

        User user;
        try{
            user = userMapper.getUser(nid);
        } catch (Exception e) {
            return response.responseError(10015);
        }

        response.setCode(0);
        response.setMsg("success");
        response.setAddress(addressStr);
        response.setUser(user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 地址溯源
    @Override
    public ResponseEntity<?> traceAddress(String queryAddress) throws Exception {
        TraceAddressResponse response = new TraceAddressResponse();

        // Step0. check if the address is suspended
        try{
            int pos = getIndexOf(queryAddress, ":", 4);
            String aidStr = queryAddress.substring(pos+1);
            aidStr = parseAddressSuffix(aidStr, 16);
            String AID = aidStr.replace(":","");
            int status = userMapper.getAIDStatus(AID);
            if(status == 2){
                return response.responseError(10023);
            }
        } catch (Exception e){
            return response.responseError(10012);
        }

        // step1. use prefix of the IPv6-address and calculate time-Hash to get key
        int pos = getIndexOf(queryAddress, ":", 4);
        String asPrefix = queryAddress.substring(0,pos);
        String asAddress = asPrefix + "::1";
        String aidStr = queryAddress.substring(pos+1);
        aidStr = parseAddressSuffix(aidStr, 16);
        String AID = aidStr.replace(":","");
        String AIDnTH = "";
        try {
            AIDnTH = userMapper.getAIDnTH(AID);
        } catch (Exception e){
            return response.responseError(10012);
        }
        BigInteger big1 = new BigInteger(AID, 16);
        BigInteger big2 = new BigInteger(AIDnTH, 16);
        String timeHash = String.format("%016x", big1.xor(big2));
        String ideaKey;
        try{
            ideaKey = userMapper.getIdeaKey(asAddress, timeHash);
            if (ideaKey == null) {
                return response.responseError(10013);
            }
        } catch (Exception e){
            return response.responseError(10013);
        }


        // step2. use suffix of IPv6-address to get the whole encrypt data(128-bits)
        String prefix = "";
        try{
            prefix = userMapper.getAIDnTHPrefix(AIDnTH);
        } catch (Exception e){
            return response.responseError(10012);
        }
        BigInteger big3 = new BigInteger(AIDnTH, 16);
        BigInteger big4 = new BigInteger(prefix, 16);
        String suffix = String.format("%016x", big3.xor(big4));
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

        User user;
        try {
            user = userMapper.getUser(nid);
        } catch (Exception e) {
            return response.responseError(10012);
        }
        UserAddress userAddress = new UserAddress();
        userAddress.setUsername(user.getUsername());
        userAddress.setPassword(user.getPassword());
        userAddress.setPhoneNumber(user.getPhoneNumber());
        userAddress.setName(user.getName());
        userAddress.setNid(user.getNid());
        userAddress.setEmailAddress(user.getEmailAddress());
        userAddress.setRole(user.getRole());
        userAddress.setStatus(user.getStatus());
        userAddress.setAddress(queryAddress);
        userAddress.setRegisterTime(String.valueOf(registerTime));

        response.setCode(0);
        response.setMsg("success");
        response.setUser(userAddress);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 地址停用
    @Override
    public ResponseEntity<?> suspendAddress(Address address) throws Exception {
        Response response = new Response();

        String suspendAddr = address.getAddress();
        int pos = getIndexOf(suspendAddr, ":", 4);
        String aidStr = suspendAddr.substring(pos+1);
        aidStr = parseAddressSuffix(aidStr, 16);
        String AID = aidStr.replace(":","");

        try{
            userMapper.suspendAID(AID);
        } catch (Exception e){
            return response.responseError(10021);
        }

        response.setCode(0);
        response.setMsg("success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 地址删除
    @Override
    public ResponseEntity<?> deleteAddress(String address) throws Exception {
        Response response = new Response();

        int pos = getIndexOf(address, ":", 4);
        String aidStr = address.substring(pos+1);
        aidStr = parseAddressSuffix(aidStr, 16);
        String AID = aidStr.replace(":","");

        try{
            userMapper.deleteAID(AID);
        } catch (Exception e){
            return response.responseError(10016);
        }

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
}
