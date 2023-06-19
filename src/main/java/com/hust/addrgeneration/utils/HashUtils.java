package com.hust.addrgeneration.utils;
import org.bouncycastle.crypto.digests.SM3Digest;

public class HashUtils {
    public static String SM3Hash(String data) {
        SM3Digest sm3Digest = new SM3Digest();
        byte[] dataBytes = data.getBytes();
        sm3Digest.update(dataBytes, 0, dataBytes.length);
        byte [] md = new byte[32];
        sm3Digest.doFinal(md, 0);
        return ConvertUtils.getHexString(md, true);
    }
}
