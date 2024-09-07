package com.carrot.carrotmarketclonecoding.auth.controller;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.REFRESH_TOKEN_NOT_MATCH;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.LOGIN_SUCCESS;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.RECREATE_TOKENS_SUCCESS;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carrot.carrotmarketclonecoding.auth.dto.TokenDto;
import com.carrot.carrotmarketclonecoding.auth.service.LoginService;
import com.carrot.carrotmarketclonecoding.common.exception.RefreshTokenNotMatchException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = KakaoLoginController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class KakaoLoginControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private LoginService loginService;

    @Test
    @DisplayName("카카오 redirect-uri 테스트")
    void callback() throws Exception {
        // given
        TokenDto tokens = TokenDto.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();

        // when
        when(loginService.login(anyString())).thenReturn(tokens);

        // then
        mvc.perform(get("/callback")
                .param("code", "code"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", equalTo(200)))
                .andExpect(jsonPath("$.result", equalTo(true)))
                .andExpect(jsonPath("$.message", equalTo(LOGIN_SUCCESS.getMessage())))
                .andExpect(jsonPath("$.data.accessToken", equalTo(tokens.getAccessToken())))
                .andExpect(jsonPath("$.data.refreshToken", equalTo(tokens.getRefreshToken())));
    }

    @Test
    @DisplayName("리프레시 토큰 갱신 테스트")
    void refresh() throws Exception {
        // given
        TokenDto tokens = TokenDto.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();

        // when
        when(loginService.refresh(anyString())).thenReturn(tokens);

        // then
        mvc.perform(get("/refresh")
                .header("Authorization", "oldRefreshToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", equalTo(200)))
                .andExpect(jsonPath("$.result", equalTo(true)))
                .andExpect(jsonPath("$.message", equalTo(RECREATE_TOKENS_SUCCESS.getMessage())))
                .andExpect(jsonPath("$.data.accessToken", equalTo(tokens.getAccessToken())))
                .andExpect(jsonPath("$.data.refreshToken", equalTo(tokens.getRefreshToken())));
    }

    @Test
    @DisplayName("리프레시 토큰 갱신시 서버의 토큰과 일치하지 않을 경우 예외 발생 테스트")
    void refreshFailRefreshTokenNotMatch() throws Exception {
        // given
        TokenDto tokens = TokenDto.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();

        // when
        doThrow(RefreshTokenNotMatchException.class).when(loginService).refresh(anyString());

        // then
        mvc.perform(get("/refresh")
                        .header("Authorization", "oldRefreshToken"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", equalTo(401)))
                .andExpect(jsonPath("$.result", equalTo(false)))
                .andExpect(jsonPath("$.message", equalTo(REFRESH_TOKEN_NOT_MATCH.getMessage())))
                .andExpect(jsonPath("$.data", equalTo(null)));
    }
}