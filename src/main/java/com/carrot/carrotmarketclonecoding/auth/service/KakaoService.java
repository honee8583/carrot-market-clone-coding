package com.carrot.carrotmarketclonecoding.auth.service;

import com.carrot.carrotmarketclonecoding.auth.dto.LoginRespDto;
import com.carrot.carrotmarketclonecoding.common.exception.KakaoTokenNotExistsException;
import com.carrot.carrotmarketclonecoding.common.exception.KakaoUserInfoNotExistsException;
import com.carrot.carrotmarketclonecoding.auth.dto.KakaoTokenResponseDto;
import com.carrot.carrotmarketclonecoding.auth.dto.KakaoUserInfoResponseDto;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.member.domain.enums.Role;
import com.carrot.carrotmarketclonecoding.member.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {

    @Value("${kakao.client_id}")
    private String clientId;

    private static final String KAUTH_TOKEN_URL_HOST = "https://kauth.kakao.com/oauth/token";
    private static final String KAUTH_USER_URL_HOST = "https://kapi.kakao.com/v2/user/me";
    private static final String KAKAO_REQUEST_HEADER = "Bearer ";
    private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    private static final String PROPERTY_NICKNAME = "nickname";
    private static final String PROPERTY_PROFILE_IMAGE = "profile_image";
    private static final String PARAM_GRANT_TYPE = "grant_type";
    private static final String PARAM_CLIENT_ID = "client_id";
    private static final String PARAM_CODE = "code";
    private static final String GRANT_TYPE_VALUE = "authorization_code";

    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate;

    public String getAccessToken(String code) {
        String url = UriComponentsBuilder.fromHttpUrl(KAUTH_TOKEN_URL_HOST)
                .queryParam(PARAM_GRANT_TYPE, GRANT_TYPE_VALUE)
                .queryParam(PARAM_CLIENT_ID, clientId)
                .queryParam(PARAM_CODE, code)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<KakaoTokenResponseDto> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                KakaoTokenResponseDto.class
        );

        KakaoTokenResponseDto kakaoTokenResponseDto = responseEntity.getBody();
        if (kakaoTokenResponseDto == null) {
            throw new KakaoTokenNotExistsException();
        }

        log.debug(" [Kakao Service] Access Token ------> {}", kakaoTokenResponseDto.getAccessToken());
        log.debug(" [Kakao Service] Refresh Token ------> {}", kakaoTokenResponseDto.getRefreshToken());
        log.debug(" [Kakao Service] Id Token ------> {}", kakaoTokenResponseDto.getIdToken());
        log.debug(" [Kakao Service] Scope ------> {}", kakaoTokenResponseDto.getScope());

        return kakaoTokenResponseDto.getAccessToken();
    }

    public KakaoUserInfoResponseDto getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, KAKAO_REQUEST_HEADER + accessToken);
        headers.set(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<KakaoUserInfoResponseDto> responseEntity = restTemplate.exchange(
                KAUTH_USER_URL_HOST,
                HttpMethod.GET,
                entity,
                KakaoUserInfoResponseDto.class
        );

        KakaoUserInfoResponseDto userInfo = responseEntity.getBody();
        if (userInfo == null) {
            throw new KakaoUserInfoNotExistsException();
        }

        log.debug("[ Kakao Service ] Auth ID ---> {} ", userInfo.getId());
        log.debug("[ Kakao Service ] NickName ---> {} ", userInfo.getProperties().get(PROPERTY_NICKNAME));
        log.debug("[ Kakao Service ] ProfileImageUrl ---> {} ", userInfo.getProperties().get(PROPERTY_PROFILE_IMAGE));

        return userInfo;
    }

    public LoginRespDto join(KakaoUserInfoResponseDto userInfo) {
        Optional<Member> optionalMember = memberRepository.findByAuthId(userInfo.getId());
        Member member = null;

        if (optionalMember.isPresent()) {
            member = optionalMember.get();
        } else {
            member = memberRepository.save(Member.builder()
                    .authId(userInfo.getId())
                    .nickname(userInfo.getProperties().get(PROPERTY_NICKNAME))
                    .profileUrl(userInfo.getProperties().get(PROPERTY_PROFILE_IMAGE))
                    .town(null)
                    .withdraw(false)
                    .isTownAuthenticated(false)
                    .role(Role.USER)
                    .build());
        }

        return LoginRespDto.builder()
                .authId(member.getAuthId())
                .role(member.getRole())
                .build();
    }
}
