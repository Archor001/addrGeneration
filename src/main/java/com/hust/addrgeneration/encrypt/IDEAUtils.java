package com.hust.addrgeneration.encrypt;

import com.hust.addrgeneration.utils.ConvertUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.Security;

public class IDEAUtils {
    public static final String KEY_ALGORITHM = "IDEA";
    public static final String CIPHER_ALGORITHM = "IDEA/ECB/ZeroBytePadding";

    public static byte[] initKey() throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);

        kg.init(128);

        SecretKey secretKey = kg.generateKey();

        return secretKey.getEncoded();
    }

    public static Key toKey(byte[] key) throws Exception {
        return new SecretKeySpec(key, KEY_ALGORITHM);
    }

    /**
     * 加密数据
     * @param data 待加密数据
     * @param key 密钥
     * @return byte[] 加密后的数据
     * */
    private static byte[] encrypt(byte[] data,byte[] key) throws Exception{
        //加入bouncyCastle支持
        Security.addProvider(new BouncyCastleProvider());
        //还原密钥
        Key k=toKey(key);
        //实例化
        Cipher cipher=Cipher.getInstance(CIPHER_ALGORITHM);
        //初始化，设置为加密模式
        cipher.init(Cipher.ENCRYPT_MODE, k);
        //执行操作
        return cipher.doFinal(data);
    }
    /**
     * 解密数据
     * @param data 待解密数据
     * @param key 密钥
     * @return byte[] 解密后的数据
     * */
    private static byte[] decrypt(byte[] data,byte[] key) throws Exception{
        //加入bouncyCastle支持
        Security.addProvider(new BouncyCastleProvider());
        //还原密钥
        Key k =toKey(key);
        Cipher cipher=Cipher.getInstance(CIPHER_ALGORITHM);
        //初始化，设置为解密模式
        cipher.init(Cipher.DECRYPT_MODE, k);
        //执行操作
        return cipher.doFinal(data);
    }
    public static String getKey() throws Exception {
        String result = null;

        result = ConvertUtils.byteToHexString(initKey());

        return result;
    }
    public static String ideaEncrypt(String data, String key) {
        String result = null;
        try {
            byte[] data_en = encrypt(ConvertUtils.hexStringToByte(data), ConvertUtils.hexStringToByte(key));
            result = ConvertUtils.byteToHexString(data_en);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String ideaDecrypt(String data, String key) {
        String result = null;
        try {
            byte[] data_de = decrypt(ConvertUtils.hexStringToByte(data), ConvertUtils.hexStringToByte(key));
            result = ConvertUtils.byteToHexString(data_de);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    public static void main(String[] args) throws Exception {
        String data = "D19E5AB3852AF37D";
        String key = getKey(); // 自动生成
        System.out.println("密钥：" + key);
        String data_en = ideaEncrypt(data, key);
        System.out.println("密文："+data_en);
        String data_de = ideaDecrypt(data_en, key);
        System.out.println("原文："+data_de);
    }
}
