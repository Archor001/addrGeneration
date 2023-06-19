package com.hust.addrgeneration.utils;

import java.math.BigInteger;

public class ConvertUtils {
    public static String patchHexString(String str, int maxLength) {
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < maxLength - str.length(); i++) {
            temp.insert(0, "0");
        }
        str = (temp + str).substring(0, maxLength);
        return str;
    }

    public static String patchBinString(String str, int maxLength) {
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < maxLength - str.length(); i++) {
            temp.insert(0,"0");
        }
        str = (temp + str).substring(0,maxLength);
        return str;
    }

    public static String getHexString(byte[] bytes, boolean upperCase) {
        StringBuilder ret = new StringBuilder();
        for (byte aByte : bytes) {
            ret.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }
        return upperCase ? ret.toString().toUpperCase() : ret.toString();
    }

    public static String decToHexString(int dec, int maxLength) {
        String result = "";
        result = Integer.toHexString(dec);
        if (result.length() % 2 == 1) {
            result = "0" + result;
        }

        return patchHexString(result, maxLength);
    }

    public static String decToBinString(int dec, int maxLength) {
        String result = "";
        result = Integer.toBinaryString(dec);
        return patchBinString(result, maxLength);
    }

    public static int binStringToDec(String binString) {
        BigInteger bigInteger = new BigInteger(binString, 2);
        return Integer.parseInt(binString.toString());
    }

    public static String binStringToHexString(String binString) {
        String result = "";
        int len = binString.length();
        if (len % 4 != 0) {
            int addNum = 4 - (len % 4);
            StringBuilder binStringBuilder = new StringBuilder(binString);
            for (int i = 0; i < addNum; i++)
                binStringBuilder.insert(0, "0");
            binString = binStringBuilder.toString();
        }

        for (int i = 0; i < binString.length(); i+=4) {
            String bin = binString.substring(i,i+4);
            switch (bin) {
                case "0000":
                    result += "0";
                    break;
                case "0001":
                    result += "1";
                    break;
                case "0010":
                    result += "2";
                    break;
                case "0011":
                    result += "3";
                    break;
                case "0100":
                    result += "4";
                    break;
                case "0101":
                    result += "5";
                    break;
                case "0110":
                    result += "6";
                    break;
                case "0111":
                    result += "7";
                    break;
                case "1000":
                    result += "8";
                    break;
                case "1001":
                    result += "9";
                    break;
                case "1010":
                    result += "A";
                    break;
                case "1011":
                    result += "B";
                    break;
                case "1100":
                    result += "C";
                    break;
                case "1101":
                    result += "D";
                    break;
                case "1110":
                    result += "E";
                    break;
                case "1111":
                    result += "F";
                    break;
            }
        }
        return result;
    }

    public static String hexStringToBinString(String hexStr) {
        hexStr = hexStr.toUpperCase();
        String result = "";
        int max = hexStr.length();
        for (int i = 0; i < max; i++) {
            char c = hexStr.charAt(i);
            switch (c) {
                case '0':
                    result += "0000";
                    break;
                case '1':
                    result += "0001";
                    break;
                case '2':
                    result += "0010";
                    break;
                case '3':
                    result += "0011";
                    break;
                case '4':
                    result += "0100";
                    break;
                case '5':
                    result += "0101";
                    break;
                case '6':
                    result += "0110";
                    break;
                case '7':
                    result += "0111";
                    break;
                case '8':
                    result += "1000";
                    break;
                case '9':
                    result += "1001";
                    break;
                case 'A':
                    result += "1010";
                    break;
                case 'B':
                    result += "1011";
                    break;
                case 'C':
                    result += "1100";
                    break;
                case 'D':
                    result += "1101";
                    break;
                case 'E':
                    result += "1110";
                    break;
                case 'F':
                    result += "1111";
                    break;
            }
        }
        return result;
    }

    public static byte[] hexStringToByte(String hex)
            throws IllegalArgumentException {
        if (hex.length() % 2 != 0) {
            hex = "0" + hex;
        }
        char[] arr = hex.toCharArray();
        byte[] b = new byte[hex.length() / 2];
        for (int i = 0, j = 0, l = hex.length(); i < l; i++, j++) {
            String swap = "" + arr[i++] + arr[i];
            int byteint = Integer.parseInt(swap, 16) & 0xFF;
            b[j] = new Integer(byteint).byteValue();
        }
        return b;
    }

    public static String byteToHexString(byte b[]) {
        if (b == null) {
            throw new IllegalArgumentException(
                    "Argument b ( byte array ) is null! ");
        }
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xff);
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs.toLowerCase();
    }

    public static byte[] byteConvert32Bytes(BigInteger n) {
        byte tmpd[] = (byte[]) null;
        if (n == null) {
            return null;
        }

        if (n.toByteArray().length == 33) {
            tmpd = new byte[32];
            System.arraycopy(n.toByteArray(), 1, tmpd, 0, 32);
        } else if (n.toByteArray().length == 32) {
            tmpd = n.toByteArray();
        } else {
            tmpd = new byte[32];
            for (int i = 0; i < 32 - n.toByteArray().length; i++) {
                tmpd[i] = 0;
            }
            System.arraycopy(n.toByteArray(), 0, tmpd, 32 - n.toByteArray().length, n.toByteArray().length);
        }
        return tmpd;
    }
}
