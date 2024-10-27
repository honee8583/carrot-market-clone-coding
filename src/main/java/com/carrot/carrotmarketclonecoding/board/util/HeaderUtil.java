package com.carrot.carrotmarketclonecoding.board.util;

import jakarta.servlet.http.HttpServletRequest;

public class HeaderUtil {

    private static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";
    private static final String HEADER_USER_AGENT = "User-Agent";

    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader(HEADER_X_FORWARDED_FOR);
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static String getUserAgent(HttpServletRequest request) {
        return request.getHeader(HEADER_USER_AGENT);
    }
}
