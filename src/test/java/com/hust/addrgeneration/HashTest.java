package com.hust.addrgeneration;

import com.hust.addrgeneration.utils.HashUtils;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class HashTest {

    @Test
    public void test() {
        String rawData = "M202088667135888888HUST_NCCD216886679d46ffc63da4e28f";
        long time1 = System.nanoTime();
        String hashcode = HashUtils.SM3Hash(rawData);
        long time2 = System.nanoTime();
        System.out.println(time2 - time1);
        System.out.println(hashcode);

    }
}
