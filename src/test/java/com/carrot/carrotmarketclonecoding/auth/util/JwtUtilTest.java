package com.carrot.carrotmarketclonecoding.auth.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.carrot.carrotmarketclonecoding.auth.dto.JwtVO;
import com.carrot.carrotmarketclonecoding.auth.dto.LoginUser;
import com.carrot.carrotmarketclonecoding.member.domain.enums.Role;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    private static final String SECRET = "testSecret";
    private static final int EXPIRATION_TIME = 60000;
    private static final long REFRESH_EXPIRATION_TIME = 1209600000L;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(jwtUtil, "SECRET", SECRET);
        ReflectionTestUtils.setField(jwtUtil, "EXPIRATION_TIME", EXPIRATION_TIME);
        ReflectionTestUtils.setField(jwtUtil, "REFRESH_EXPIRATION_TIME", REFRESH_EXPIRATION_TIME);
    }

    @Test
    @DisplayName("JWT 토큰 생성 테스트")
    public void createAccessToken() {
        // given
        Long authId = 1L;
        Role role = Role.USER;

        // when
        String token = jwtUtil.createAccessToken(authId, role);

        // then
        assertThat(token).isNotNull();
        assertThat(token.startsWith(JwtVO.TOKEN_PREFIX)).isTrue();
    }

    @Test
    @DisplayName("리프레시 토큰 생성 테스트")
    public void testCreateRefreshToken() {
        // given
        Long authId = 1L;
        Role role = Role.USER;

        // when
        String token = jwtUtil.createRefreshToken(authId, role);

        // then
        assertThat(token).isNotNull();
        assertThat(token.startsWith(JwtVO.TOKEN_PREFIX)).isTrue();
    }

    @Test
    @DisplayName("토큰 검증 테스트")
    public void testVerify() {
        // given
        Long authId = 1L;
        Role role = Role.USER;

        String token = JWT.create()
                .withSubject("token")
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .withClaim("id", authId)
                .withClaim("role", role.name())
                .sign(Algorithm.HMAC512(SECRET));

        token = JwtVO.TOKEN_PREFIX + token;

        // when
        LoginUser loginUser = jwtUtil.verify(token);

        // then
        assertThat(loginUser).isNotNull();
        assertThat(authId).isEqualTo(loginUser.getMember().getAuthId());
        assertThat(role).isEqualTo(loginUser.getMember().getRole());
    }

    @Test
    @DisplayName("올바른 토큰이 아닐경우 예외 발생 테스트")
    public void verifyFailTokenNotValid() {
        // given
        String invalidToken = JwtVO.TOKEN_PREFIX + "invalidToken";

        // when
        // then
        assertThatThrownBy(() -> jwtUtil.verify(invalidToken))
                .isInstanceOf(JWTVerificationException.class);
    }
}