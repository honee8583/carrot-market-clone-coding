package com.carrot.carrotmarketclonecoding.auth.service;

import com.carrot.carrotmarketclonecoding.auth.dto.KakaoTokenResponseDto;
import com.carrot.carrotmarketclonecoding.auth.dto.KakaoUserInfoResponseDto;
import com.carrot.carrotmarketclonecoding.common.exception.KakaoTokenNotExistsException;
import com.carrot.carrotmarketclonecoding.common.exception.KakaoUserInfoNotExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {

    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${kakao.admin-key}")
    private String adminKey;

    private static final String KAUTH_TOKEN_URL_HOST = "https://kauth.kakao.com/oauth/token";
    private static final String KAUTH_USER_URL_HOST = "https://kapi.kakao.com/v2/user/me";
    private static final String KAUTH_UNLINK_URL_HOST = "https://kapi.kakao.com/v1/user/unlink";
    private static final String KAUTH_LOGOUT_URL_HOST = "https://kapi.kakao.com/v1/user/logout";

    private static final String PARAM_GRANT_TYPE = "grant_type";
    private static final String PARAM_CLIENT_ID = "client_id";
    private static final String PARAM_CODE = "code";
    private static final String GRANT_TYPE_VALUE = "authorization_code";
    private static final String PARAM_TARGET_ID_TYPE = "target_id_type";
    private static final String PARAM_TARGET_ID = "target_id";
    private static final String VALUE_USER_ID = "user_id";

    private static final String UNLINK_AND_LOGOUT_HEADER_PREFIX = "KakaoAK ";

    private final RestTemplate restTemplate;

    public String getAccessToken(String code) {
        String url = UriComponentsBuilder.fromHttpUrl(KAUTH_TOKEN_URL_HOST)
                .queryParam(PARAM_GRANT_TYPE, GRANT_TYPE_VALUE)
                .queryParam(PARAM_CLIENT_ID, clientId)
                .queryParam(PARAM_CODE, code)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

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

        return kakaoTokenResponseDto.getAccessToken();
    }

    public KakaoUserInfoResponseDto getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

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

        return userInfo;
    }

    public void logout(Long authId) {
        kakaoRequest(authId, KAUTH_LOGOUT_URL_HOST);
    }

    public void unlink(Long authId) {
        kakaoRequest(authId, KAUTH_UNLINK_URL_HOST);
    }

    private void kakaoRequest(Long authId, String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set(HttpHeaders.AUTHORIZATION, UNLINK_AND_LOGOUT_HEADER_PREFIX + adminKey);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(PARAM_TARGET_ID_TYPE, VALUE_USER_ID);
        params.add(PARAM_TARGET_ID, String.valueOf(authId));

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
        ResponseEntity<String> result = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                String.class);

        log.debug("[ Kakao Service ] unlink, logout result ---> {} ", result.getBody());
    }
}
