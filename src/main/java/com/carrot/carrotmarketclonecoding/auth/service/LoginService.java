package com.carrot.carrotmarketclonecoding.auth.service;

import com.carrot.carrotmarketclonecoding.auth.dto.KakaoUserInfoResponseDto;
import com.carrot.carrotmarketclonecoding.auth.dto.LoginRespDto;
import com.carrot.carrotmarketclonecoding.auth.dto.LoginUser;
import com.carrot.carrotmarketclonecoding.auth.dto.TokenDto;
import com.carrot.carrotmarketclonecoding.auth.util.JwtUtil;
import com.carrot.carrotmarketclonecoding.board.repository.BoardLikeRepository;
import com.carrot.carrotmarketclonecoding.board.repository.BoardRepository;
import com.carrot.carrotmarketclonecoding.common.exception.MemberNotFoundException;
import com.carrot.carrotmarketclonecoding.common.exception.RefreshTokenNotMatchException;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.member.domain.enums.Role;
import com.carrot.carrotmarketclonecoding.member.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private static final String PROPERTY_NICKNAME = "nickname";
    private static final String PROPERTY_PROFILE_IMAGE = "profile_image";

    private final JwtUtil jwtUtil;
    private final KakaoService kakaoService;
    private final RefreshTokenRedisService refreshTokenRedisService;
    private final MemberRepository memberRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final BoardRepository boardRepository;

    public TokenDto login(String code) {
        LoginRespDto loginResponse = kakaoLogin(code);
        Long authId = loginResponse.getAuthId();
        TokenDto tokens = createTokenDto(authId, loginResponse.getRole());
        refreshTokenRedisService.saveRefreshToken(authId, tokens.getRefreshToken());
        return tokens;
    }

    public TokenDto refresh(String refreshToken) {
        LoginUser user = jwtUtil.verify(refreshToken);
        Long authId = Long.parseLong(user.getUsername());
        String savedRefreshToken = refreshTokenRedisService.getRefreshToken(authId);
        if (!savedRefreshToken.equals(refreshToken)) {
            throw new RefreshTokenNotMatchException();
        }

        TokenDto tokens = createTokenDto(authId, user.getMember().getRole());
        refreshTokenRedisService.deleteRefreshToken(authId);
        refreshTokenRedisService.saveRefreshToken(authId, tokens.getRefreshToken());
        return tokens;
    }

    public void logout(Long authId) {
        kakaoService.logout(authId);
        refreshTokenRedisService.deleteRefreshToken(authId);
    }

    @Transactional
    public void withdraw(Long authId) {
        kakaoService.unlink(authId);
        refreshTokenRedisService.deleteRefreshToken(authId);
        deleteAllMemberData(authId);
    }

    private LoginRespDto kakaoLogin(String code) {
        String kakaoAccessToken = kakaoService.getAccessToken(code);
        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(kakaoAccessToken);
        return joinIfMemberNotExits(userInfo);
    }

    private LoginRespDto joinIfMemberNotExits(KakaoUserInfoResponseDto userInfo) {
        Optional<Member> optionalMember = memberRepository.findByAuthId(userInfo.getId());
        Member member = null;

        if (optionalMember.isPresent()) {
            member = optionalMember.get();
        } else {
            member = saveMemberWithKakaoUserInfo(userInfo);
        }

        return LoginRespDto.builder()
                .authId(member.getAuthId())
                .role(member.getRole())
                .build();
    }

    private Member saveMemberWithKakaoUserInfo(KakaoUserInfoResponseDto userInfo) {
        return memberRepository.save(Member.builder()
                .authId(userInfo.getId())
                .nickname(userInfo.getProperties().get(PROPERTY_NICKNAME))
                .profileUrl(userInfo.getProperties().get(PROPERTY_PROFILE_IMAGE))
                .town(null)
                .isTownAuthenticated(false)
                .role(Role.USER)
                .build());
    }

    private TokenDto createTokenDto(Long authId, Role role) {
        return new TokenDto(jwtUtil.createAccessToken(authId, role), jwtUtil.createRefreshToken(authId, role));
    }

    private void deleteAllMemberData(Long authId) {
        Member member = memberRepository.findByAuthId(authId).orElseThrow(MemberNotFoundException::new);
        boardLikeRepository.deleteAllByMemberId(member.getId());
        boardRepository.deleteAllByMemberId(member.getId());
        memberRepository.delete(member);
    }
}
