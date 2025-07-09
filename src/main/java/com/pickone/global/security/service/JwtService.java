package com.pickone.global.security.service;

import com.mongodb.internal.VisibleForTesting;
import com.pickone.domain.user.entity.User;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import com.pickone.global.security.entity.UserPrincipal;
import com.pickone.global.security.repository.RefreshTokenRepository;
import com.pickone.global.security.repository.TokenBlacklistRepository;
import com.pickone.global.security.token.TokenProvider;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService implements TokenProvider {

  private final TokenBlacklistRepository blacklistRepo;
  private final RefreshTokenRepository refreshTokenRepo;
  private final UserJpaRepository userRepo;

  public void setJwtProperties(String secret, long accessTokenTtl, long refreshTokenTtl) {
    this.secret = secret;
    this.accessTokenTtl = accessTokenTtl;
    this.refreshTokenTtl = refreshTokenTtl;
  }

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.access-token-expiration}")
  private long accessTokenTtl;

  @Value("${jwt.refresh-token-expiration}")
  private long refreshTokenTtl;

  // === 토큰 생성 ===
  @Override
  public String generateAccessToken(UserPrincipal principal) {
    return generateToken(buildClaims(principal), principal.getUsername(), accessTokenTtl);
  }

  @Override
  public String generateRefreshToken(UserPrincipal principal) {
    String token = generateToken(new HashMap<>(), principal.getUsername(), refreshTokenTtl);
    refreshTokenRepo.save(principal.getUsername(), token, refreshTokenTtl);
    return token;
  }

  private String generateToken(Map<String, Object> claims, String subject, long ttl) {
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + ttl))
        .signWith(getKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  // === 인증 객체 추출 ===
  @Override
  public Authentication getAuthentication(String token) {
    String email = extractClaim(token, Claims::getSubject);
    User user = userRepo.findByProfileEmail(email)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
    UserPrincipal principal = UserPrincipal.from(user);
    return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
  }

  // === 리프레시 토큰 유효성 검사 ===
  @Override
  public boolean validateRefreshToken(String token) {
    try {
      parseClaims(token); // Signature/만료 여부 검증
      return refreshTokenRepo.existsByToken(token) && !blacklistRepo.isBlacklisted(token);
    } catch (JwtException e) {
      log.warn("[JwtService] 리프레시 토큰 유효성 실패: {}", e.getMessage());
      return false;
    }
  }

  @Override public boolean isTokenBlacklisted(String token) { return blacklistRepo.isBlacklisted(token); }
  @Override public void blacklistToken(String token) {
    long ttl = extractExpiration(token).getTime() - System.currentTimeMillis();
    blacklistRepo.addToBlacklist(token, ttl);
    log.info("[JwtService] 토큰 블랙리스트 추가됨: ttl={}ms", ttl);
  }

  // === 유틸 메서드 ===
  private Map<String, Object> buildClaims(UserPrincipal user) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("userId", user.getId());
    claims.put("role", user.getRole().name());
    return claims;
  }

  private Claims parseClaims(String token) {
    return Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token).getBody();
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private <T> T extractClaim(String token, Function<Claims, T> resolver) {
    return resolver.apply(parseClaims(token));
  }

  private Key getKey() {
    byte[] bytes = hexToBytes(secret);
    return Keys.hmacShaKeyFor(bytes);
  }

  private byte[] hexToBytes(String hex) {
    byte[] bytes = new byte[hex.length() / 2];
    for (int i = 0; i < hex.length(); i += 2) {
      bytes[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
          + Character.digit(hex.charAt(i + 1), 16));
    }
    return bytes;
  }

  @Override public String resolveToken(HttpServletRequest req) {
    String bearer = req.getHeader("Authorization");
    if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
      return bearer.substring(7);
    }
    return null;
  }

  @Override public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  @Override public long getAccessTokenExpiration() { return accessTokenTtl; }

  @Override public long getRefreshTokenExpiration() { return refreshTokenTtl; }

  @Override
  public Long getUserIdFromToken(String token) {
    return extractClaim(token, claims -> claims.get("userId", Long.class));
  }
}