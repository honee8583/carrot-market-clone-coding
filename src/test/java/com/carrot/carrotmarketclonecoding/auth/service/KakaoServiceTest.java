package com.carrot.carrotmarketclonecoding.auth.service;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.KAKAO_TOKEN_NOT_EXISTS;
import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.KAKAO_USER_INFO_NOT_EXISTS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.carrot.carrotmarketclonecoding.auth.dto.KakaoTokenResponseDto;
import com.carrot.carrotmarketclonecoding.auth.dto.KakaoUserInfoResponseDto;
import com.carrot.carrotmarketclonecoding.auth.dto.LoginRespDto;
import com.carrot.carrotmarketclonecoding.common.exception.KakaoTokenNotExistsException;
import com.carrot.carrotmarketclonecoding.common.exception.KakaoUserInfoNotExistsException;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class KakaoServiceTest {

    @InjectMocks
    private KakaoService kakaoService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RestTemplate restTemplate;

    @Test
    @DisplayName("카카오로 AccessToken 요청 테스트")
    void getAccessToken() {
        // given
        KakaoTokenResponseDto kakaoTokenResponseDto = new KakaoTokenResponseDto();
        kakaoTokenResponseDto.setTokenType("testTokenType");
        kakaoTokenResponseDto.setAccessToken("testAccessToken");
        kakaoTokenResponseDto.setIdToken("testIdToken");
        kakaoTokenResponseDto.setExpiresIn(30000);
        kakaoTokenResponseDto.setRefreshToken("testRefreshToken");
        kakaoTokenResponseDto.setRefreshTokenExpiresIn(30000);
        kakaoTokenResponseDto.setScope("scope");

        when(restTemplate.exchange(anyString(), any(), any(), eq(KakaoTokenResponseDto.class)))
                .thenReturn(ResponseEntity.ok(kakaoTokenResponseDto));

        // when
        String accessToken = kakaoService.getAccessToken("code");

        // then
        assertThat(accessToken).isEqualTo("testAccessToken");
    }

    @Test
    @DisplayName("카카오로부터 AccessToken이 들어오지 않을 경우 예외 발생 테스트")
    void getAccessTokenFailKakaoTokenNotExists() {
        // given
        when(restTemplate.exchange(anyString(), any(), any(), eq(KakaoTokenResponseDto.class)))
                .thenReturn(ResponseEntity.ok(null));

        // when
        // then
        assertThatThrownBy(() -> kakaoService.getAccessToken("code"))
                .isInstanceOf(KakaoTokenNotExistsException.class)
                .hasMessage(KAKAO_TOKEN_NOT_EXISTS.getMessage());
    }

    @Test
    @DisplayName("카카오로 사용자정보 요청 테스트")
    void getUserInfo() {
        // given
        KakaoUserInfoResponseDto kakaoUserInfoResponseDto = createMockKakaoUserInfoResponseDto();

        when(restTemplate.exchange(anyString(), any(), any(), eq(KakaoUserInfoResponseDto.class)))
                .thenReturn(ResponseEntity.ok(kakaoUserInfoResponseDto));

        // when
        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo("accessToken");

        // then
        assertThat(userInfo.getId()).isEqualTo(1111L);
        assertThat(userInfo.getProperties().get("nickname")).isEqualTo("testNickName");
        assertThat(userInfo.getProperties().get("profile_image")).isEqualTo("testProfileImage");
    }

    @Test
    @DisplayName("카카오로부터 사용자정보가 들어오지 않을 경우 예외 발생 테스트")
    void getUserInfoFailKakaoUserInfoNotExists() {
        // given
        when(restTemplate.exchange(anyString(), any(), any(), eq(KakaoUserInfoResponseDto.class)))
                .thenReturn(ResponseEntity.ok(null));

        // when
        // then
        assertThatThrownBy(() -> kakaoService.getUserInfo("accessToken"))
                .isInstanceOf(KakaoUserInfoNotExistsException.class)
                .hasMessage(KAKAO_USER_INFO_NOT_EXISTS.getMessage());
    }

    @Test
    @DisplayName("존재하는 회원일경우 회원가입 테스트")
    void joinWhenMemberAlreadyExists() {
        // given
        Long authId = 1111L;
        Member member = Member.builder()
                .authId(authId)
                .role(Role.USER)
                .build();

        KakaoUserInfoResponseDto userInfo = createMockKakaoUserInfoResponseDto();

        when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.of(member));

        // when
        LoginRespDto loginResponse = kakaoService.join(userInfo);

        // then
        assertThat(loginResponse.getAuthId()).isEqualTo(authId);
        assertThat(loginResponse.getRole()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("존재하지않는 회원일경우 회원가입 테스트")
    void joinWhenMemberNotExists() {
        // given
        Long authId = 1111L;

        KakaoUserInfoResponseDto userInfo = createMockKakaoUserInfoResponseDto();

        when(memberRepository.findByAuthId(anyLong())).thenReturn(Optional.empty());
        when(memberRepository.save(any())).thenReturn(Member.builder().authId(authId).role(Role.USER).build());

        // when
        LoginRespDto loginResponse = kakaoService.join(userInfo);

        // then
        assertThat(loginResponse.getAuthId()).isEqualTo(authId);
        assertThat(loginResponse.getRole()).isEqualTo(Role.USER);
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