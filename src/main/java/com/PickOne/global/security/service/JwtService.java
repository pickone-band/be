package com.PickOne.global.security.service;


import com.PickOne.domain.user.model.entity.UserEntity;
import com.PickOne.domain.user.repository.UserJpaRepository;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import com.PickOne.global.security.model.entity.UserPrincipal;
import com.PickOne.global.security.repository.TokenBlacklistRepository;
import com.PickOne.global.security.config.SecurityConstants;
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
public class JwtService implements TokenProvider{

  @Value("${jwt.secret}")
  private String secretKey;

  @Getter
  @Value("${jwt.access-token-expiration}")
  private long accessTokenExpiration;

  @Getter
  @Value("${jwt.refresh-token-expiration}")
  private long refreshTokenExpiration;

  private final TokenBlacklistRepository tokenBlacklistRepository;
  private final UserJpaRepository userJpaRepository;

  @Override
  public String generateAccessToken(UserPrincipal userDetails) {
    return generateToken(
            createClaims(userDetails), userDetails.getUsername(), accessTokenExpiration);
  }

  @Override
  public String generateRefreshToken(UserPrincipal userDetails) {
    return generateToken(new HashMap<>(), userDetails.getUsername(), refreshTokenExpiration);
  }

  @Override
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  @Override
  public boolean validateRefreshToken(String refreshToken) {
    try {
      Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(refreshToken);
      return isTokenBlacklisted(refreshToken);
    } catch (JwtException e) {
      log.error("Invalid refresh token: {}", e.getMessage());
      return false;
    }
  }

  public Long getUserIdFromToken(String token) {
    Claims claims = extractAllClaims(token);
    return claims.get("userId", Long.class);
  }

  @Override
  public boolean isTokenBlacklisted(String token) {
    return !tokenBlacklistRepository.isBlacklisted(token);
  }

  @Override
  public void blacklistToken(String token) {
    Date expiration = extractExpiration(token);
    long ttl = expiration.getTime() - System.currentTimeMillis();
    tokenBlacklistRepository.addToBlacklist(token, ttl);
  }

  @Override
  public Authentication getAuthentication(String token) {
    Claims claims = extractAllClaims(token);
    String email = claims.getSubject();

    UserEntity userEntity = userJpaRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

    UserPrincipal userPrincipal = UserPrincipal.from(userEntity);

    return new UsernamePasswordAuthenticationToken(
            userPrincipal,
            null,
            userPrincipal.getAuthorities()
    );
  }

  @Override
  public String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader(SecurityConstants.AUTH_HEADER);
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(SecurityConstants.TOKEN_PREFIX)) {
      return bearerToken.substring(SecurityConstants.TOKEN_PREFIX.length());
    }
    return null;
  }

  private Map<String, Object> createClaims(UserPrincipal userDetails) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("userId", userDetails.getUser().getId());
    claims.put("authorities", Collections.emptyList());
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
}
