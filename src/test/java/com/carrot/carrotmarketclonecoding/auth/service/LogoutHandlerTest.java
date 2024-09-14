package com.carrot.carrotmarketclonecoding.auth.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.carrot.carrotmarketclonecoding.auth.dto.LoginUser;
import com.carrot.carrotmarketclonecoding.auth.handler.LogoutHandlerImpl;
import com.carrot.carrotmarketclonecoding.auth.util.JwtUtil;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class LogoutHandlerTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private LoginService loginService;

    @Mock
    private Authentication authentication;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private LogoutHandlerImpl logoutHandler;

    @Test
    @DisplayName("로그아웃 핸들러 동작 테스트")
    void logoutSuccess() {
        // given
        Long authId = 1111L;
        String token = "token";
        when(request.getHeader(anyString())).thenReturn(token);
        when(jwtUtil.verify(anyString())).thenReturn(new LoginUser(Member.builder().authId(authId).build()));

        // when
        logoutHandler.logout(request, response, authentication);

        // then
        verify(jwtUtil).verify(token);
        verify(loginService).logout(authId);
    }
}