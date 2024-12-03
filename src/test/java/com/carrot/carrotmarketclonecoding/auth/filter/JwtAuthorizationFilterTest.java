package com.carrot.carrotmarketclonecoding.auth.filter;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.TOKEN_EXPIRED_MESSAGE;
import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.TOKEN_NOT_EXISTS;
import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.TOKEN_NOT_VALID;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.carrot.carrotmarketclonecoding.auth.dto.JwtVO;
import com.carrot.carrotmarketclonecoding.auth.dto.LoginUser;
import com.carrot.carrotmarketclonecoding.auth.util.JwtUtil;
import com.carrot.carrotmarketclonecoding.common.exception.JwtTokenExpiredException;
import com.carrot.carrotmarketclonecoding.common.exception.JwtTokenNotValidException;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.member.domain.enums.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;

@ExtendWith(MockitoExtension.class)
class JwtAuthorizationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthorizationFilter jwtAuthorizationFilter;

    @BeforeEach
    public void setUp() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
    }

    @Test
    @DisplayName("토큰이 입력되지 않을 경우 통과 테스트")
    public void doFilterTest() throws Exception {
        // given
        // when
        jwtAuthorizationFilter.doFilter(request, response, filterChain);

        // then
        verify(filterChain, atLeastOnce()).doFilter(request, response);
    }

    @Test
    @DisplayName("토큰이 존재하지 않을 경우 예외 발생 테스트")
    void doFilterFailJwtVerification() throws ServletException, IOException {
        // given
        doReturn(null).when(request).getHeader(JwtVO.HEADER);

        // when
        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(request).setAttribute(JwtVO.JWT_EXCEPTION_ATTRIBUTE, TOKEN_NOT_EXISTS.getMessage());
        verify(filterChain, atLeastOnce()).doFilter(request, response);
    }

    @Test
    @DisplayName("토큰이 만료되었을 경우 예외 발생 테스트")
    void doFilterFail() throws ServletException, IOException {
        // given
        String accessToken = "Bearer AccessToken";
        when(request.getHeader(anyString())).thenReturn(accessToken);
        when(jwtUtil.verify(anyString())).thenThrow(JwtTokenExpiredException.class);

        // when
        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(request).setAttribute(JwtVO.JWT_EXCEPTION_ATTRIBUTE, TOKEN_EXPIRED_MESSAGE.getMessage());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("토큰이 유효할경우 필터 통과 테스트")
    void doFilterSuccessJwtVerification() throws ServletException, IOException {
        // given
        String accessToken = "Bearer validAccessToken";
        LoginUser loginUser = new LoginUser(Member.builder().authId(1111L).role(Role.USER).build());

        doReturn(accessToken).when(request).getHeader(JwtVO.HEADER);
        when(jwtUtil.verify(anyString())).thenReturn(loginUser);

        // when
        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("토큰이 유효하지 않을 경우 필터 통과 테스트")
    void doFilterFailTokenNotValid() throws ServletException, IOException {
        // given
        String accessToken = "Bearer invalidAccessToken";

        doReturn(accessToken).when(request).getHeader(JwtVO.HEADER);
        when(jwtUtil.verify(anyString())).thenThrow(JwtTokenNotValidException.class);

        // when
        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(request).setAttribute(JwtVO.JWT_EXCEPTION_ATTRIBUTE, TOKEN_NOT_VALID.getMessage());
        verify(filterChain).doFilter(request, response);
    }
}