package com.carrot.carrotmarketclonecoding.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.carrot.carrotmarketclonecoding.auth.dto.KakaoUserInfoResponseDto;
import com.carrot.carrotmarketclonecoding.auth.dto.LoginRespDto;
import com.carrot.carrotmarketclonecoding.auth.dto.LoginUser;
import com.carrot.carrotmarketclonecoding.auth.dto.TokenDto;
import com.carrot.carrotmarketclonecoding.auth.util.JwtUtil;
import com.carrot.carrotmarketclonecoding.common.exception.RefreshTokenNotMatchException;
import com.carrot.carrotmarketclonecoding.common.response.FailedMessage;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.member.domain.enums.Role;
import java.util.HashMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @InjectMocks
    private LoginService loginService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private KakaoService kakaoService;

    @Mock
    private RefreshTokenRedisService redisService;

    @Test
    @DisplayName("로그인 성공 테스트")
    void loginSuccess() {
        // given
        Long authId = 1111L;
        String accessToken = "testAccessToken";
        String refreshToken = "testRefreshToken";
        LoginRespDto loginResponse = LoginRespDto.builder()
                .authId(authId)
                .role(Role.USER)
                .build();

        when(kakaoService.getAccessToken(anyString())).thenReturn("mockKakaoAccessToken");
        when(kakaoService.getUserInfo(anyString())).thenReturn(createMockKakaoUserInfoResponseDto());
        when(kakaoService.join(any())).thenReturn(loginResponse);
        when(jwtUtil.createAccessToken(anyLong(), any(Role.class))).thenReturn(accessToken);
        when(jwtUtil.createRefreshToken(anyLong(), any(Role.class))).thenReturn(refreshToken);
        doNothing().when(redisService).saveRefreshToken(anyLong(), anyString());

        // when
        TokenDto tokens = loginService.login("code");

        // then
        assertThat(tokens.getAccessToken()).isEqualTo(accessToken);
        assertThat(tokens.getRefreshToken()).isEqualTo(refreshToken);
    }

    @Test
    @DisplayName("리프레시 토큰 재발급 성공")
    void refreshSuccess() {
        // given
        LoginUser loginUser = new LoginUser(Member.builder().authId(1111L).role(Role.USER).build());
        String savedRefreshToken = "testSavedRefreshToken";
        String newAccessToken = "testNewAccessToken";
        String newRefreshToken = "testNewRefreshToken";

        when(jwtUtil.verify(anyString())).thenReturn(loginUser);
        when(redisService.getRefreshToken(anyLong())).thenReturn(savedRefreshToken);
        doNothing().when(redisService).deleteRefreshToken(anyLong());
        when(jwtUtil.createAccessToken(anyLong(), any())).thenReturn(newAccessToken);
        when(jwtUtil.createRefreshToken(anyLong(), any())).thenReturn(newRefreshToken);
        doNothing().when(redisService).saveRefreshToken(anyLong(), anyString());

        // when
        TokenDto tokens = loginService.refresh(savedRefreshToken);

        // then
        assertThat(tokens.getAccessToken()).isEqualTo(newAccessToken);
        assertThat(tokens.getRefreshToken()).isEqualTo(newRefreshToken);
    }

    @Test
    @DisplayName("사용자 기존 리프레시 토큰과 요청한 리프레시 토큰이 일치하지 않을 경우 예외 발생")
    void refreshFailRefreshTokenNotMatch() {
        // given
        LoginUser loginUser = new LoginUser(Member.builder().authId(1111L).role(Role.USER).build());

        when(jwtUtil.verify(anyString())).thenReturn(loginUser);
        when(redisService.getRefreshToken(anyLong())).thenReturn("testSavedRefreshToken");

        // when
        // then
        assertThatThrownBy(() -> loginService.refresh("wrongRefreshToken"))
                .isInstanceOf(RefreshTokenNotMatchException.class)
                .hasMessage(FailedMessage.REFRESH_TOKEN_NOT_MATCH.getMessage());
    }

    private KakaoUserInfoResponseDto createMockKakaoUserInfoResponseDto() {
        HashMap<String, String> properties = new HashMap<>();
        properties.put("nickname", "testNickName");
        properties.put("profile_image", "testProfileImage");

        KakaoUserInfoResponseDto userInfo = new KakaoUserInfoResponseDto();
        userInfo.setId(1111L);
        userInfo.setProperties(properties);

        return userInfo;
    }
}