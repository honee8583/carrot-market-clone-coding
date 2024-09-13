package com.carrot.carrotmarketclonecoding.auth.exception;

import com.carrot.carrotmarketclonecoding.auth.dto.JwtVO;
import com.carrot.carrotmarketclonecoding.auth.util.ResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        if (request.getAttribute(JwtVO.JWT_EXCEPTION_ATTRIBUTE) != null) {
            ResponseUtil.fail(response, request.getAttribute(JwtVO.JWT_EXCEPTION_ATTRIBUTE).toString(), HttpStatus.UNAUTHORIZED);
        }
    }
}
