package com.carrot.carrotmarketclonecoding.auth.service;

import com.carrot.carrotmarketclonecoding.auth.dto.JwtVO;
import com.carrot.carrotmarketclonecoding.auth.dto.LoginUser;
import com.carrot.carrotmarketclonecoding.auth.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Slf4j
@RequiredArgsConstructor
public class LogoutHandlerImpl implements LogoutHandler {
    private final JwtUtil jwtUtil;
    private final RefreshTokenRedisService redisService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        log.debug("LogoutHandler 동작중...");

        LoginUser loginUser = jwtUtil.verify(request.getHeader(JwtVO.HEADER));
        redisService.deleteRefreshToken(Long.parseLong(loginUser.getUsername()));
    }
}
