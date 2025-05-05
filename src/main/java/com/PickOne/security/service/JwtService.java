package com.PickOne.security.service;

import com.PickOne.security.model.entity.SecurityUser;
import com.PickOne.security.repository.TokenBlacklistRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

  @Value("${jwt.secret}")
  private String secretKey;

  @Getter
  @Value("${jwt.access-token-expiration}")
  private long accessTokenExpiration;

  @Getter
  @Value("${jwt.refresh-token-expiration}")
  private long refreshTokenExpiration;

  private final TokenBlacklistRepository tokenBlacklistRepository;
  private final CustomUserDetailsService userDetailsService;

  public String generateAccessToken(SecurityUser userDetails) {
    return generateToken(
        createClaims(userDetails), userDetails.getUsername(), accessTokenExpiration);
  }

  public String generateRefreshToken(SecurityUser userDetails) {
    return generateToken(new HashMap<>(), userDetails.getUsername(), refreshTokenExpiration);
  }

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public boolean validateRefreshToken(String refreshToken) {
    try {
      // Verify token signature and expiration date
      Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(refreshToken);

      // Check if the token is NOT blacklisted (valid)
      return !isTokenBlacklisted(refreshToken);
    } catch (JwtException e) {
      log.error("Invalid refresh token: {}", e.getMessage());
      return false;
    }
  }

  public Long getUserIdFromToken(String token) {
    Claims claims = extractAllClaims(token);
    return claims.get("userId", Long.class);
  }

  public boolean isTokenBlacklisted(String token) {
    return !tokenBlacklistRepository.isBlacklisted(token);
  }

  public void blacklistToken(String token) {
    Date expiration = extractExpiration(token);
    long ttl = expiration.getTime() - System.currentTimeMillis();
    tokenBlacklistRepository.addToBlacklist(token, ttl);
  }

  public Authentication getAuthentication(String token) {
    Claims claims = extractAllClaims(token);
    Long userId = claims.get("userId", Long.class);

    try {
      // UserDetailsService를 통해 사용자 정보 조회
      SecurityUser securityUser = userDetailsService.loadUserById(userId);

      // SecurityUser의 권한 정보를 사용하여 인증 객체 생성
      return new UsernamePasswordAuthenticationToken(
          securityUser, null, securityUser.getAuthorities());
    } catch (UsernameNotFoundException e) {
      log.error("인증 정보 생성 중 오류: 사용자를 찾을 수 없습니다. ID: {}", userId);

      // 사용자를 찾을 수 없는 경우 토큰의 기본 정보로 인증 객체 생성
      List<String> authorities = claims.get("authorities", List.class);
      List<GrantedAuthority> grantedAuthorities =
          authorities != null
              ? authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
              : new ArrayList<>();

      return new UsernamePasswordAuthenticationToken(claims.getSubject(), null, grantedAuthorities);
    }
  }

  public String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }

  private Map<String, Object> createClaims(SecurityUser userDetails) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("userId", userDetails.getUserId());

    // 권한 정보 추가
    List<String> authorities =
        userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());
    claims.put("authorities", authorities);

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
      // 만료된 토큰의 경우에도 클레임 정보는 필요할 수 있음
      return e.getClaims();
    }
  }

  private Key getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}

