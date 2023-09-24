package com.hust.addrgeneration.utils;

public class AddressUtils {
    public static int getAddressBitLength(String addr){
        int length = 0;         // bit长度
        int lastPos = 0;        // 最后冒号出现的位置
        for(int i=0; i<addr.length(); i++){
            if(addr.charAt(i) == ':'){
                length = length + 4;
                lastPos = i;
            }
        }
        for(int i=lastPos+1;i<addr.length();i++){
            length++;
        }
        return 4 * length;
    }
}
