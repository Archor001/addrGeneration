package com.hust.addrgeneration.serviceImpl;

import com.hust.addrgeneration.beans.InfoBean;
import com.hust.addrgeneration.encrypt.SM2.SM2Utils;
import com.hust.addrgeneration.service.IPv6Service;
import com.hust.addrgeneration.utils.ConvertUtils;
import com.hust.addrgeneration.utils.EncDecUtils;
import com.hust.addrgeneration.utils.HashUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class IPv6ServiceImpl implements IPv6Service {
    private static final Logger logger = LoggerFactory.getLogger(IPv6ServiceImpl.class);

    @Override
    public String ipv6AddrGeneration(InfoBean userInfo) throws Exception {
        String userID = userInfo.getUserID();
        String passwd = userInfo.getPassword();
        String phoneNumber = userInfo.getPhoneNumber();


        // step1. Calculate NID with user's information
        String encryptStr = userID + phoneNumber;
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
        String NID = userPart + organizationPart;

        // step2. Calculate the time information
        LocalDateTime localDateTime1 = LocalDateTime.now();
        LocalDateTime localDateTime2 = LocalDateTime.of(localDateTime1.getYear(), 1, 1, 0, 0, 0);

        long currentTime = localDateTime1.toEpochSecond(ZoneOffset.of("+8"));
        long baseTime = localDateTime2.toEpochSecond(ZoneOffset.of("+8"));

        /*
         the raw time difference maybe over 24-bits, so we use proper degree of accuracy to change the time difference into a proper format
         the degrees of accuracy can be 30s 10s or 5s
         the max value of time difference is 31,536,000, the max value of 24-bits is 16,777,215
         so every degree can make the value of time difference smaller than the max value of 24-bits
         */
        int timeDifference = (int) (( currentTime - baseTime ) / 10);
        String timeInformation = ConvertUtils.decToBinString(timeDifference, 24);
        String preSuffix = ConvertUtils.binStringToHexString(NID) + ConvertUtils.binStringToHexString(timeInformation);

        // step3. Generate AID with NID and time information
        String preAID = EncDecUtils.ideaEncrypt(preSuffix, EncDecUtils.ideaKey);
        String str1 = preAID.substring(0,16);
        String str2 = preAID.substring(17,32);

        BigInteger big1 = new BigInteger(str1, 16);
        BigInteger big2 = new BigInteger(str2, 16);
        String AID = big1.xor(big2).toString(16);

        StringBuilder suffix = new StringBuilder();
        for (int i = 0; i < AID.length(); i+=4) {
            suffix.append(AID, i, i + 4).append(":");
        }
        String asPrefix = "2001:250:4000:511a:";
        return asPrefix + suffix.substring(0,suffix.length()-1);
    }

    @Override
    public String ipv6AddrRebind(InfoBean userInfo) {
        return null;
    }

    @Override
    public String ipv6AddrUnbind(String userID) {
        return null;
    }

    @Override
    public String showAllBindTable() {
        return null;
    }
}
