package com.hust.addrgeneration.utils;

import com.hust.addrgeneration.beans.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ErrorUtils {

    public static String message(int code) {
        switch (code) {
            case 10001:
                return "Need Login";
            case 10002:
                return "Register Failed";
            case 10003:
                return "Address Generation Failed";
            case 10004:
                return "PhoneNumber has not been registered";
            case 10005:
                return "Wrong password";
            case 10006:
                return "Please register NID";
            case 10007:
                return "Failed to get key-set. Key-set empty";
            case 10008:
                return "Decryption Failed";
            case 10009:
                return "Encryption Failed";
            case 10010:
                return "Failed to get user";
            case 10011:
                return "current phoneNumber has already applied address";
            case 10012:
                return "invalid phoneNumber";
            case 10013:
                return "Failed to delete user";
            case 10014:
                return "This NID corresponds to non-exist user";
            case 10015:
                return "phoneNumber has been registered already";
            case 10016:
                return "QueryAddress doesn't exist";
            case 10017:
                return "PhoneNumber has not registered NID yet";
            case 10018:
                return "Please create ISP-prefix in the platform first";
            default:
                return "Unknown";
        }
    }
}
