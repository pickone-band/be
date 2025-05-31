package com.PickOne.global.security.service;

import com.PickOne.domain.user.model.domain.Email;
import com.PickOne.domain.user.model.domain.Password;
import com.PickOne.domain.user.model.domain.User;
import com.PickOne.global.security.model.entity.UserPrincipal;
import com.PickOne.global.security.repository.TokenBlacklistRepository;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Mock
    private TokenBlacklistRepository tokenBlacklistRepository;

    @Mock
    private CustomUserDetailsService userDetailsService;

    private Key signingKey;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        String secret = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";
        ReflectionTestUtils.setField(jwtService, "secretKey", secret);
        ReflectionTestUtils.setField(jwtService, "accessTokenExpiration", 1000L * 60 * 60);       // 1 hour
        ReflectionTestUtils.setField(jwtService, "refreshTokenExpiration", 1000L * 60 * 60 * 24); // 1 day

        this.signingKey = (Key) ReflectionTestUtils.invokeMethod(jwtService, "getSigningKey");
    }

    @Test
    @DisplayName("Refresh Token 유효성 검증 성공")
    void validateRefreshToken_success() {
        User user = new User(1L, Email.of("refresh@example.com"), Password.ofEncoded("pw"), "닉", true);
        UserPrincipal principal = UserPrincipal.from(user);

        String refreshToken = jwtService.generateRefreshToken(principal);

        when(tokenBlacklistRepository.isBlacklisted(refreshToken)).thenReturn(true);

        boolean result = jwtService.validateRefreshToken(refreshToken);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Access Token 생성 및 userId/email 추출")
    void generateAndExtractToken() {
        User user = new User(2L, Email.of("user@example.com"), Password.ofEncoded("pw"), "유저", true);
        UserPrincipal principal = UserPrincipal.from(user);

        String accessToken = jwtService.generateAccessToken(principal);

        Long userId = jwtService.getUserIdFromToken(accessToken);
        String email = jwtService.extractUsername(accessToken);

        assertThat(userId).isEqualTo(user.getId());
        assertThat(email).isEqualTo(user.getEmail().getValue());
    }

    @Test
    @DisplayName("블랙리스트 토큰 처리 성공")
    void blacklistToken_success() {
        User user = new User(3L, Email.of("bl@example.com"), Password.ofEncoded("pw"), "닉", true);
        UserPrincipal principal = UserPrincipal.from(user);

        String token = jwtService.generateAccessToken(principal);

        Claims claims = io.jsonwebtoken.Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Date exp = claims.getExpiration();
        long ttl = exp.getTime() - System.currentTimeMillis();

        jwtService.blacklistToken(token);

        verify(tokenBlacklistRepository).addToBlacklist(eq(token), anyLong());
    }
}
