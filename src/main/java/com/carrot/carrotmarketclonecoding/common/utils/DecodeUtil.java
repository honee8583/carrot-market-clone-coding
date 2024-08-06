package com.carrot.carrotmarketclonecoding.common.utils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class DecodeUtil {

    public static String decodeByUtf8(String url) {
        return URLDecoder.decode(url, StandardCharsets.UTF_8);
    }
}
