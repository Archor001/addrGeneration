package com.hust.addrgeneration.serviceImpl;

import com.hust.addrgeneration.beans.KeyInfo;
import com.hust.addrgeneration.dao.UserMapper;
import com.hust.addrgeneration.encrypt.IDEAUtils;
import com.hust.addrgeneration.utils.ConvertUtils;
import com.hust.addrgeneration.utils.EncDecUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@EnableScheduling
public class KeyGenerateServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(KeyGenerateServiceImpl.class);
    final UserMapper userMapper;

    public KeyGenerateServiceImpl(UserMapper userMapper) throws Exception {
        this.userMapper = userMapper;
        if (!isHaveKey()) {
            this.updateKey();
        }
    }

    public boolean isHaveKey() throws Exception {
        LocalDateTime localDateTime1 = LocalDateTime.now();
        LocalDateTime localDateTime2 = LocalDateTime.of(localDateTime1.getYear(),1,1,0,0,0);
        LocalDateTime localDateTime3 = LocalDateTime.of(localDateTime1.getYear(),localDateTime1.getMonth(),localDateTime1.getDayOfMonth(),localDateTime1.getHour(),0,0);
        long currentTime = localDateTime3.toEpochSecond(ZoneOffset.of("+8"));
        long baseTime = localDateTime2.toEpochSecond(ZoneOffset.of("+8"));
        int timeInfo = (int) (currentTime - baseTime);
        String timeHash = EncDecUtils.md5Encrypt16(ConvertUtils.decToHexString(timeInfo,10));
        String ideaKey = userMapper.getIdeaKey("2001:250:4000:4507::1", timeHash);
        if (ideaKey != null) {
            EncDecUtils.ideaKey = ideaKey;
            return true;
        } else {
            return false;
        }
    }


    @Scheduled(cron = "0 0 0/1 * * ? ")
    public void updateKey() throws Exception {
        EncDecUtils.ideaKey = IDEAUtils.getKey();
        LocalDateTime localDateTime1 = LocalDateTime.now();
        LocalDateTime localDateTime2 = LocalDateTime.of(localDateTime1.getYear(),1,1,0,0,0);
        LocalDateTime localDateTime3 = LocalDateTime.of(localDateTime1.getYear(),localDateTime1.getMonth(),localDateTime1.getDayOfMonth(),localDateTime1.getHour(),0,0);
        long currentTime = localDateTime3.toEpochSecond(ZoneOffset.of("+8"));
        long baseTime = localDateTime2.toEpochSecond(ZoneOffset.of("+8"));
        int timeInfo = (int) (currentTime - baseTime);
        String timeHash = EncDecUtils.md5Encrypt16(ConvertUtils.decToHexString(timeInfo,10));

        logger.info("时间" + timeInfo);
        String genAddrIP = "2001:250:4000:4507::1";

        KeyInfo keyInfo =new KeyInfo();
        keyInfo.setAddrGenIP(genAddrIP);
        keyInfo.setIdeaKey(EncDecUtils.ideaKey);
        keyInfo.setTimeHash(timeHash);
        userMapper.updateKey(keyInfo);
    }

}
