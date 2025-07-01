package com.pickone.global.security.service;

import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import com.pickone.global.security.model.entity.UserPrincipal;
import com.pickone.global.security.repository.RefreshTokenRepository;
import com.pickone.global.security.repository.TokenBlacklistRepository;
import com.pickone.global.security.config.SecurityConstants;
import com.pickone.global.security.token.TokenProvider;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService implements TokenProvider {

  @Value("${jwt.secret}")
  private String secretKey;

  @Value("${jwt.access-token-expiration}")
  private long accessTokenExpiration;

  @Value("${jwt.refresh-token-expiration}")
  private long refreshTokenExpiration;

  private final TokenBlacklistRepository tokenBlacklistRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final UserJpaRepository userJpaRepository;

  @Override
  public String generateAccessToken(UserPrincipal userDetails) {
    return generateToken(createClaims(userDetails), userDetails.getUsername(), accessTokenExpiration);
  }

  @Override
  public String generateRefreshToken(UserPrincipal userDetails) {
    String refreshToken = generateToken(new HashMap<>(), userDetails.getUsername(), refreshTokenExpiration);
    refreshTokenRepository.save(userDetails.getUsername(), refreshToken, refreshTokenExpiration);
    return refreshToken;
  }

  @Override
  public long getAccessTokenExpiration() {
    return accessTokenExpiration;
  }

  @Override
  public long getRefreshTokenExpiration() {
    return refreshTokenExpiration;
  }

  @Override
  public Authentication getAuthentication(String token) {
    Claims claims = extractAllClaims(token);
    String email = claims.getSubject();

    UserEntity userEntity = userJpaRepository.findByProfileEmail(email)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

    UserPrincipal userPrincipal = UserPrincipal.from(userEntity);

    return new UsernamePasswordAuthenticationToken(
        userPrincipal,
        null,
        userPrincipal.getAuthorities()
    );
  }

  @Override
  public boolean validateRefreshToken(String refreshToken) {
    try {
      Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(refreshToken);
      boolean valid = refreshTokenRepository.existsByToken(refreshToken) && isTokenBlacklisted(
          refreshToken);
      if (!valid) {
        log.warn("리프레시 토큰이 블랙리스트 또는 저장소에 없음");
      }
      return valid;
    } catch (JwtException e) {
      log.error("유효하지 않은 리프레시 토큰: {}", e.getMessage());
      return false;
    }
  }

  @Override
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  @Override
  public String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader(SecurityConstants.AUTH_HEADER);
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(SecurityConstants.TOKEN_PREFIX)) {
      return bearerToken.substring(SecurityConstants.TOKEN_PREFIX.length());
    }
    return null;
  }

  @Override
  public void blacklistToken(String token) {
    Date expiration = extractExpiration(token);
    long ttl = expiration.getTime() - System.currentTimeMillis();
    log.info("토큰 블랙리스트 추가: ttl={}ms", ttl);
    tokenBlacklistRepository.addToBlacklist(token, ttl);
  }

  @Override
  public boolean isTokenBlacklisted(String token) {
    return !tokenBlacklistRepository.isBlacklisted(token);
  }

  // ===== 내부 JWT/Claims 유틸 =====

  private Map<String, Object> createClaims(UserPrincipal userDetails) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("userId", userDetails.getId());
    claims.put("role", userDetails.getRole().name());
    // authorities, 프로필, 추가 커스텀 claim 필요시 확장
    return claims;
  }

  public String generateToken(Map<String, Object> extraClaims, String subject, long expiration) {
    return Jwts.builder()
        .setClaims(extraClaims)
        .setSubject(subject)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    try {
      return Jwts.parserBuilder()
          .setSigningKey(getSigningKey())
          .build()
          .parseClaimsJws(token)
          .getBody();
    } catch (ExpiredJwtException e) {
      return e.getClaims();
    }
  }

  private Key getSigningKey() {
    byte[] keyBytes = hexStringToByteArray(secretKey); // 기존 BASE64 디코딩 → Hex 디코딩
    return Keys.hmacShaKeyFor(keyBytes);
  }

  private byte[] hexStringToByteArray(String hex) {
    int len = hex.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
          + Character.digit(hex.charAt(i + 1), 16));
    }
    return data;
  }

  public Long getUserIdFromToken(String token) {
    return extractClaim(token, claims -> claims.get("userId", Long.class));
  }

  // ===== 추가적으로 필요하면 여기에 보조 기능 구현 =====
}
