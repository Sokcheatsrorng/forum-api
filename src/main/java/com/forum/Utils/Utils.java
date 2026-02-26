package com.forum.Utils;


import jakarta.servlet.http.HttpServletRequest;

import java.security.SecureRandom;
import java.util.UUID;

public class Utils {

    public static String generateUuid() {
        return UUID.randomUUID().toString();
    }

    public static String extractExtension(String mediaName) {
        return mediaName.substring(mediaName.lastIndexOf("."));
    }

    public static String getApplicationUrl(HttpServletRequest request) {
        return request.getRequestURL().toString().replace(request.getServletPath(), "");
    }

    public static String generateDigitsToken() {
        SecureRandom random = new SecureRandom();
        int tokenLength = 6;
        StringBuilder token = new StringBuilder(tokenLength);

        for (int i = 0; i < tokenLength; i++) {
            int digit = random.nextInt(10);
            token.append(digit);
        }
        return token.toString();
    }

}

