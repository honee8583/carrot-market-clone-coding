package com.carrot.carrotmarketclonecoding.auth.service;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

import com.carrot.carrotmarketclonecoding.auth.util.ResponseUtil;
import com.carrot.carrotmarketclonecoding.common.response.SuccessMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class LogoutSuccessHandlerImplTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private LogoutSuccessHandlerImpl logoutSuccessHandler;

    @Test
    @DisplayName("로그아웃 성공 핸들러 테스트")
    void onLogoutSuccess() throws Exception {
        // given
        String message = SuccessMessage.LOGOUT_SUCCESS.getMessage();

        // when
        // then
        try (MockedStatic<ResponseUtil> mockedStatic = mockStatic(ResponseUtil.class)) {
            // when
            logoutSuccessHandler.onLogoutSuccess(request, response, authentication);

            // then
            mockedStatic.verify(() -> ResponseUtil.success(eq(response), eq(message), eq(null)));
        }
    }
}