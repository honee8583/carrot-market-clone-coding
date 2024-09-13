package com.carrot.carrotmarketclonecoding.auth.util;

import com.carrot.carrotmarketclonecoding.common.response.ResponseResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class ResponseUtil {

    public static void success(HttpServletResponse response, String msg, Object data) {
        try {
            ObjectMapper om = new ObjectMapper();
            ResponseResult result = ResponseResult.success(HttpStatus.OK, msg, data);
            String responseBody = om.writeValueAsString(result);

            response.setContentType("application/json; charset=utf-8");
            response.setStatus(200);
            response.getWriter().println(responseBody);
        } catch (Exception e) {
            log.error("server parsing error");
        }
    }

    public static void fail(HttpServletResponse response, String msg, HttpStatus httpStatus) {
        try {
            ObjectMapper om = new ObjectMapper();
            ResponseResult result = ResponseResult.failed(httpStatus, msg, null);
            String responseBody = om.writeValueAsString(result);

            response.setContentType("application/json; charset=utf-8");
            response.setStatus(httpStatus.value());
            response.getWriter().println(responseBody);
        } catch (Exception e) {
            log.error("server parsing error");
        }
    }
}