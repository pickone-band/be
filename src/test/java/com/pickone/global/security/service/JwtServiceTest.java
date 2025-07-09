package com.pickone.global.security.service;

import com.pickone.domain.user.entity.User;
import com.pickone.domain.user.model.domain.Gender;
import com.pickone.domain.user.model.vo.*;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.common.enums.Mbti;
import com.pickone.global.security.entity.UserPrincipal;
import com.pickone.global.security.repository.RefreshTokenRepository;
import com.pickone.global.security.repository.TokenBlacklistRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

  @InjectMocks
  private JwtService jwtService;

  @Mock
  private RefreshTokenRepository refreshTokenRepo;

  @Mock
  private TokenBlacklistRepository blacklistRepo;

  @Mock
  private UserJpaRepository userRepo;

  private User user;
  private UserPrincipal principal;

  @BeforeEach
  void setUp() {

    jwtService = new JwtService(blacklistRepo, refreshTokenRepo, userRepo);
    jwtService.setJwtProperties(
        "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437",
        3600000L,
        604800000L
    );

    user = User.create("test@example.com", "pw", "nickname", Gender.MALE,
        LocalDate.of(1990, 1, 1), Mbti.INFP, List.of(), null, null);
    principal = UserPrincipal.from(user);
  }

  private UserPrincipal mockPrincipal() {
    User user = User.create("user@example.com", "pw", "nick", Gender.MALE,
        LocalDate.now(), Mbti.ENTP, List.of(), null, null);
    return UserPrincipal.from(user);
  }

  @Test
  void generateAccessToken_shouldReturnValidJwt() {
    UserPrincipal principal = mockPrincipal();
    String token = jwtService.generateAccessToken(principal);

    assertNotNull(token);
    String subject = jwtService.extractUsername(token);
    assertEquals(principal.getUsername(), subject);
  }

  @Test
  void generateRefreshToken_shouldStoreToken() {
    UserPrincipal principal = mockPrincipal();
    String token = jwtService.generateRefreshToken(principal);

    assertNotNull(token);
    verify(refreshTokenRepo).save(eq(principal.getUsername()), eq(token), anyLong());
  }

  @Test
  void getAuthentication_validToken_shouldReturnAuthentication() {
    UserPrincipal principal = mockPrincipal();
    String token = jwtService.generateAccessToken(principal);

    when(userRepo.findByProfileEmail(principal.getUsername()))
        .thenReturn(Optional.of(User.create("user@example.com", "pw", "nick", Gender.MALE,
            LocalDate.now(), Mbti.ISFP, List.of(), null, null)));

    assertDoesNotThrow(() -> jwtService.getAuthentication(token));
  }

  @Test
  void validateRefreshToken_valid_shouldReturnTrue() {
    UserPrincipal principal = mockPrincipal();
    String token = jwtService.generateRefreshToken(principal);

    when(refreshTokenRepo.existsByToken(token)).thenReturn(true);
    when(blacklistRepo.isBlacklisted(token)).thenReturn(false);

    assertTrue(jwtService.validateRefreshToken(token));
  }

  @Test
  void validateRefreshToken_invalid_shouldReturnFalse() {
    String invalidToken = "malformed.token.value";
    assertFalse(jwtService.validateRefreshToken(invalidToken));
  }

  @Test
  void blacklistToken_shouldAddToBlacklistWithCorrectTTL() {
    UserPrincipal principal = mockPrincipal();
    String token = jwtService.generateAccessToken(principal);

    jwtService.blacklistToken(token);
    verify(blacklistRepo).addToBlacklist(eq(token), anyLong());
  }

  @Test
  void resolveToken_shouldExtractTokenFromHeader() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader("Authorization")).thenReturn("Bearer abc.def.ghi");

    String token = jwtService.resolveToken(request);
    assertEquals("abc.def.ghi", token);
  }

  @Test
  void getUserIdFromToken_shouldExtractUserId() {
    UserPrincipal principal = mockPrincipal();
    String token = jwtService.generateAccessToken(principal);
    Long userId = jwtService.getUserIdFromToken(token);
    assertEquals(principal.getId(), userId);
  }
}
