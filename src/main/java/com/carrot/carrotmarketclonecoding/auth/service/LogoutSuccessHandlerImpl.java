package com.carrot.carrotmarketclonecoding.auth.service;

import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.LOGOUT_SUCCESS;

import com.carrot.carrotmarketclonecoding.auth.util.ResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Slf4j
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        log.debug("로그아웃 성공...");
        ResponseUtil.success(response, LOGOUT_SUCCESS.getMessage(), null);
    }
}
