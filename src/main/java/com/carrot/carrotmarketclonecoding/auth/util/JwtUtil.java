package com.carrot.carrotmarketclonecoding.auth.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.carrot.carrotmarketclonecoding.auth.dto.JwtVO;
import com.carrot.carrotmarketclonecoding.auth.dto.LoginUser;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.member.domain.enums.Role;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String SECRET;

    @Value("${jwt.expiration_time}")
    private int EXPIRATION_TIME;

    @Value("${jwt.refresh_expiration_time}")
    private long REFRESH_EXPIRATION_TIME;

    private static final String SUBJECT = "token";
    private static final String CLAIM_ID = "id";
    private static final String CLAIM_ROLE = "role";

    public String createAccessToken(Long authId, Role role) {
        return create(authId, role, EXPIRATION_TIME);
    }

    public String createRefreshToken(Long authId, Role role) {
        return create(authId, role, REFRESH_EXPIRATION_TIME);
    }

    private String create(Long authId, Role role, long expirationTime) {
        String jwtToken = JWT.create()
                .withSubject(SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
                .withClaim(CLAIM_ID, authId)
                .withClaim(CLAIM_ROLE, role.name())
                .sign(Algorithm.HMAC512(SECRET));
        return JwtVO.TOKEN_PREFIX + jwtToken;
    }

    public LoginUser verify(String token) {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(SECRET))
                .build()
                .verify(token.replace(JwtVO.TOKEN_PREFIX, ""));

        Member member = Member.builder()
                .authId(decodedJWT.getClaim(CLAIM_ID).asLong())
                .role(Role.valueOf(decodedJWT.getClaim(CLAIM_ROLE).asString()))
                .build();

        return new LoginUser(member);
    }
}