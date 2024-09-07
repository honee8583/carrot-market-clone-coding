package com.carrot.carrotmarketclonecoding.auth.service;

import com.carrot.carrotmarketclonecoding.auth.dto.KakaoUserInfoResponseDto;
import com.carrot.carrotmarketclonecoding.auth.dto.LoginRespDto;
import com.carrot.carrotmarketclonecoding.auth.dto.LoginUser;
import com.carrot.carrotmarketclonecoding.auth.dto.TokenDto;
import com.carrot.carrotmarketclonecoding.auth.util.JwtUtil;
import com.carrot.carrotmarketclonecoding.common.exception.RefreshTokenNotMatchException;
import com.carrot.carrotmarketclonecoding.member.domain.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final JwtUtil jwtUtil;
    private final KakaoService kakaoService;
    private final RefreshTokenRedisService redisService;

    public TokenDto login(String code) {
        String kakaoAccessToken = kakaoService.getAccessToken(code);
        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(kakaoAccessToken);
        LoginRespDto loginResponse = kakaoService.join(userInfo);

        Long authId = loginResponse.getAuthId();
        Role role = loginResponse.getRole();
        String accessToken = jwtUtil.createAccessToken(authId, role);
        String refreshToken = jwtUtil.createRefreshToken(authId, role);
        redisService.saveRefreshToken(authId, refreshToken);

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public TokenDto refresh(String refreshToken) {
        LoginUser user = jwtUtil.verify(refreshToken);
        Long authId = user.getMember().getAuthId();
        Role role = user.getMember().getRole();

        String savedRefreshToken = redisService.getRefreshToken(authId);
        if (!savedRefreshToken.equals(refreshToken)) {
            throw new RefreshTokenNotMatchException();
        }

        redisService.deleteRefreshToken(authId);

        String accessToken = jwtUtil.createAccessToken(authId, role);
        String newRefreshToken = jwtUtil.createRefreshToken(authId, role);
        redisService.saveRefreshToken(authId, newRefreshToken);

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}
