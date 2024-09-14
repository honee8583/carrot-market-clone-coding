package com.carrot.carrotmarketclonecoding.auth.controller;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.MEMBER_NOT_FOUND;
import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.REFRESH_TOKEN_NOT_MATCH;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.LOGIN_SUCCESS;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.RECREATE_TOKENS_SUCCESS;
import static com.carrot.carrotmarketclonecoding.common.response.SuccessMessage.WITHDRAW_SUCCESS;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carrot.carrotmarketclonecoding.auth.config.WithCustomMockUser;
import com.carrot.carrotmarketclonecoding.auth.dto.TokenDto;
import com.carrot.carrotmarketclonecoding.auth.service.LoginService;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.RefreshTokenNotMatchException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

@WithCustomMockUser
@WebMvcTest(controllers = KakaoLoginController.class)
class KakaoLoginControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private LoginService loginService;

    @Test
    @DisplayName("카카오 로그인 테스트")
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
    @DisplayName("JWT 토큰 갱신 테스트")
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
    @DisplayName("JWT 토큰 갱신시 요청한 리프레시 토큰과 서버의 리프레시 토큰과 일치하지 않을 경우 예외 발생 테스트")
    void refreshFailRefreshTokenNotMatch() throws Exception {
        // given
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

    @Test
    @DisplayName("카카오 및 서비스 회원 탈퇴 테스트")
    void withdraw() throws Exception {
        // given
        doNothing().when(loginService).withdraw(anyLong());

        // when
        // then
        mvc.perform(post("/withdraw")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(jsonPath("$.status", equalTo(200)))
                .andExpect(jsonPath("$.result", equalTo(true)))
                .andExpect(jsonPath("$.message", equalTo(WITHDRAW_SUCCESS.getMessage())))
                .andExpect(jsonPath("$.data", equalTo(null)));
    }

    @Test
    @DisplayName("존재하지 않는 회원이 회원탈퇴 요청을 한경우")
    void withdrawFailMemberNotFound() throws Exception {
        // given
        doThrow(MemberNotFoundException.class).when(loginService).withdraw(anyLong());

        // when
        // then
        mvc.perform(post("/withdraw")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(jsonPath("$.status", equalTo(401)))
                .andExpect(jsonPath("$.result", equalTo(false)))
                .andExpect(jsonPath("$.message", equalTo(MEMBER_NOT_FOUND.getMessage())))
                .andExpect(jsonPath("$.data", equalTo(null)));
    }
}