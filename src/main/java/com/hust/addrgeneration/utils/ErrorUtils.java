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
                return "Wrong Password";
            case 10003:
                return "Failed to create user";
            default:
                return "Unknown";
        }
    }
}