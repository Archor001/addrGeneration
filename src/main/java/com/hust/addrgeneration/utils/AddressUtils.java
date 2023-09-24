package com.hust.addrgeneration.utils;

public class AddressUtils {
    // 获取ISP地址的前缀长度，bit为单位
    public static int getAddressBitLength(String addr){
        int length = 0;         // bit长度
        for(int i=0; i<addr.length(); i++){
            if(addr.charAt(i) == ':'){
                length = length + 4;
            }
        }
        length = length + 4;
        return 4 * length;
    }

    // 地址解析入库（加前导0）
    public static String parseAddressToString(String addr, int byteLength){
        String part = "";
        String rnt = "";
        int bytes = 0;
        for(int i=0;i<addr.length();i++){
            if(addr.charAt(i) == ':'){
                part = String.format("%4s", part).replace(" ","0");
                rnt = rnt + part;
                bytes = bytes + 4;
                part = "";
                continue;
            }
            part = part + addr.charAt(i);
        }
        switch(byteLength - bytes){
            case 4:
                rnt = rnt + String.format("%4s", part).replace(" ", "0");
                break;
            case 3:
                rnt = rnt + String.format("%3s", part).replace(" ","0");
                break;
            case 2:
                rnt = rnt + String.format("%2s", part).replace(" ","0");
                break;
            case 1:
                rnt = rnt + String.format("%1s", part).replace(" ","0");
                break;
            default:
                break;
        }
        return rnt;
    }

    // 地址出库，前端展示（去前导0）
    public static String displayAddress(String addr){
        String part = "";
        String rnt = "";
        for(int i=0;i<addr.length();i++){
            if(addr.charAt(i) == ':'){
                part = part.replaceFirst("^0*","");
                rnt = rnt + part + ":";
                part = "";
                continue;
            }
            part = part + addr.charAt(i);
        }
        rnt = rnt + part.replaceFirst("^0*","");
        return rnt;
    }
}
