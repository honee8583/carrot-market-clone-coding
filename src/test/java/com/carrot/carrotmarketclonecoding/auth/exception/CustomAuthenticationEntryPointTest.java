package com.carrot.carrotmarketclonecoding.auth.exception;

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.carrot.carrotmarketclonecoding.auth.dto.JwtVO;
import com.carrot.carrotmarketclonecoding.auth.util.LoginResponseUtil;
import com.carrot.carrotmarketclonecoding.common.response.FailedMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationEntryPointTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException authException;

    @InjectMocks
    private CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Test
    @DisplayName("커스텀 AuthenticationEntryPoint 테스트")
    void commence() throws Exception {
        // given
        String message = FailedMessage.TOKEN_NOT_VALID.getMessage();

        when(request.getAttribute(JwtVO.JWT_EXCEPTION_ATTRIBUTE)).thenReturn(message);

        try (MockedStatic<LoginResponseUtil> mockedStatic = mockStatic(LoginResponseUtil.class)) {
            // when
            authenticationEntryPoint.commence(request, response, authException);

            // then
            mockedStatic.verify(() -> LoginResponseUtil.fail(eq(response), eq(message), eq(HttpStatus.UNAUTHORIZED)));
        }
    }

}