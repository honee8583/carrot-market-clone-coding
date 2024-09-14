package com.carrot.carrotmarketclonecoding.auth.service;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.MEMBER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.carrot.carrotmarketclonecoding.auth.dto.KakaoUserInfoResponseDto;
import com.carrot.carrotmarketclonecoding.auth.dto.LoginUser;
import com.carrot.carrotmarketclonecoding.auth.dto.TokenDto;
import com.carrot.carrotmarketclonecoding.auth.util.JwtUtil;
import com.carrot.carrotmarketclonecoding.board.repository.BoardLikeRepository;
import com.carrot.carrotmarketclonecoding.board.repository.BoardRepository;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.RefreshTokenNotMatchException;
import com.carrot.carrotmarketclonecoding.common.response.FailedMessage;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.member.domain.enums.Role;
import com.carrot.carrotmarketclonecoding.member.repository.MemberRepository;
import java.util.HashMap;
import java.util.Optional;
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

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BoardLikeRepository boardLikeRepository;

    @Mock
    private BoardRepository boardRepository;

    @Test
    @DisplayName("로그인 성공 테스트")
    void loginSuccess() {
        // given
        String accessToken = "testAccessToken";
        String refreshToken = "testRefreshToken";

        when(kakaoService.getAccessToken(anyString())).thenReturn("mockKakaoAccessToken");
        when(kakaoService.getUserInfo(anyString())).thenReturn(createMockKakaoUserInfoResponseDto());
        when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mock(Member.class)));
        when(jwtUtil.createAccessToken(anyLong(), any())).thenReturn(accessToken);
        when(jwtUtil.createRefreshToken(anyLong(), any())).thenReturn(refreshToken);
        doNothing().when(redisService).saveRefreshToken(anyLong(), anyString());

        // when
        TokenDto tokens = loginService.login("code");

        // then
        assertThat(tokens.getAccessToken()).isEqualTo(accessToken);
        assertThat(tokens.getRefreshToken()).isEqualTo(refreshToken);
    }

    @Test
    @DisplayName("JWT 토큰 재발급 성공")
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

    @Test
    @DisplayName("로그아웃 성공 테스트")
    void logoutSuccess() {
        // given
        Long authId = 1111L;

        // when
        loginService.logout(authId);

        // then
        verify(kakaoService).logout(authId);
        verify(redisService).deleteRefreshToken(authId);
    }

    @Test
    @DisplayName("회원탈퇴 성공 테스트")
    void withdrawSuccess() {
        // given
        Long id = 1L;
        Long authId = 1111L;
        Member mockMember = Member.builder()
                .id(1L)
                .authId(authId)
                .build();
        when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(mockMember));

        // when
        loginService.withdraw(authId);

        // then
        verify(kakaoService).unlink(authId);
        verify(redisService).deleteRefreshToken(authId);
        verify(boardLikeRepository).deleteAllByMemberId(id);
        verify(boardRepository).deleteAllByMemberId(id);
        verify(memberRepository).delete(mockMember);
        verify(redisService).deleteRefreshToken(authId);
    }

    @Test
    @DisplayName("회원탈퇴 실패 - 사용자가 존재하지 않음")
    void withdrawFailMemberNotFound() {
        // given
        when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> loginService.withdraw(1111L))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessage(MEMBER_NOT_FOUND.getMessage());
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