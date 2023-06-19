package com.hust.addrgeneration.utils;

import com.hust.addrgeneration.encrypt.IDEAUtils;
import com.hust.addrgeneration.encrypt.SM2.SM2Utils;
import com.hust.addrgeneration.encrypt.RSAUtils;

import java.security.MessageDigest;

public class EncDecUtils {
    final static String prikSM = "4cf170068e9c47ebdb521fb9fc62c4a55a5773fb9da33b0acf8129e28d09d205";
    final static String pubkSM = "04aabda53043e8dcb86d42f690b61a4db869821dadf9f851ec3c5c43d0c8f95a6677fdba984afc3bb010a8436b1d17cefc2011a34e01e9e801124d29ffa928d803";

    final static String pubkRSA = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCz1zqQHtHvKczHh58ePiRNgOyiHEx6lZDPlvwBTaHmkNlQyyJ06SIlMU1pmGKxILjT7n06nxG7LlFVUN5MkW/jwF39/+drkHM5B0kh+hPQygFjRq81yxvLwolt+Vq7h+CTU0Z1wkFABcTeQQldZkJlTpyx0c3+jq0o47wIFjq5fwIDAQAB";
    final static String prikRSA = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALPXOpAe0e8pzMeHnx4+JE2A7KIcTHqVkM+W/AFNoeaQ2VDLInTpIiUxTWmYYrEguNPufTqfEbsuUVVQ3kyRb+PAXf3/52uQczkHSSH6E9DKAWNGrzXLG8vCiW35WruH4JNTRnXCQUAFxN5BCV1mQmVOnLHRzf6OrSjjvAgWOrl/AgMBAAECgYAgA0YHdZUFL7mmIvwuE/2+Vh7JVKRAhfM7ILNHQBx7wHkOqro9eWp8mGQhUeDvitWb1C4yizJK0Znkx/pqQtFZuoatUsggocjXFl86FElQwrBp08DvfKfd0bGgy0VTFQVmCtxiqhpAmC7xmXNZXfBD41rl9CKbFfZw05QC5BoQ0QJBAO7LSku97NgFBJQ+vbmVDonuvgnQjVNb7SnwrcpJHEUAGbaVq1a50jz+s6n39TOagASaW6pcY0uwiygYu6xDnkMCQQDAzIGNKFKomTI6djcOyHfQ1ZXqyDQ3guX6nHhzZnNHFF8ZD3fPyyIRSZ3JvPK5iEzJLhB7FRtyWkGcdXgJTWoVAkBfx9zKGqkYUJLwn2XcPWRygPdq2mMFb5bmPqqGu+KB7rNhoBD0nV4tpwALifCpPSxiLEPeRmZxoqN+dsU4KHsfAkAyQt4fK3zpAQ8MGJdf3jkGEzhC/bBHLHPB8pqgEvxIcnIcOWEVpbIa6aMd3Yk1fuftpnmbbLQ8CnWCUUlau3jFAkEAk6bOZIWhTYRwIZcwBdkpyLlbatQFoTTM3i444YutXt3FrFfaWBxge+eYKId+J4dCrt/EmHhSfWKEzHibf6N5Sg==";

    public static String ideaKey;

    public static String sMEncrypt(String sourceData) throws Exception {
        return SM2Utils.encrypt(ConvertUtils.hexStringToByte(pubkSM), sourceData.getBytes());
    }

    public static String sMDecrypt(String encryptData) throws Exception {
        String decrypt = null;

        try {
            decrypt = new String(SM2Utils.decrypt(ConvertUtils.hexStringToByte(prikSM), ConvertUtils.hexStringToByte(encryptData)));
        } catch (Exception e) {
            throw new Exception("加解密验证失败！");
        }
        return decrypt;
    }

    public static String rsaEncrypt(String sourceData) throws Exception {
        return RSAUtils.encrypt(sourceData, pubkRSA);
    }

    public static String rsaDecrypt(String encryptData) throws Exception {
        String decrypt = null;

        try {
            decrypt = RSAUtils.decrypt(encryptData,prikRSA);
        } catch (Exception e) {
            throw new Exception("加解密验证失败！");
        }
        return decrypt;
    }


    public static String ideaEncrypt(String sourceData, String ideaKey) throws Exception {
        return IDEAUtils.ideaEncrypt(sourceData, ideaKey);
    }

    public static String ideaDecrypt(String decryptData, String ideaKey) throws Exception {
        return IDEAUtils.ideaDecrypt(decryptData, ideaKey);
    }


    public static String md5Encrypt16(String string) {
        return md5Encrypt32(string).substring(8, 24);
    }

    public static String md5Encrypt32(String encryptStr) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md5.digest(encryptStr.getBytes());
            StringBuffer hexValue = new StringBuffer();
            for (int i = 0; i < md5Bytes.length; i++) {
                int val = ((int) md5Bytes[i]) & 0xff;
                if (val < 16)
                    hexValue.append("0");
                hexValue.append(Integer.toHexString(val));
            }
            encryptStr = hexValue.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return encryptStr;
    }
}