package com.pickone.global.security.service;

import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenService {

  private final StringRedisTemplate redisTemplate;
  private static final long TOKEN_EXPIRATION_SECONDS = 3600; // 1시간 유효

  // 토큰 생성 후 email과 매핑하여 Redis에 저장
  public String createAndSaveToken(String email) {
    String token = UUID.randomUUID().toString();
    redisTemplate.opsForValue().set(token, email, TOKEN_EXPIRATION_SECONDS, TimeUnit.SECONDS);
    return token;
  }

  // 토큰 유효성 체크 및 email 반환 (없으면 예외 발생)
  public String validateTokenAndGetEmail(String token) {
    String email = redisTemplate.opsForValue().get(token);
    if (email == null) {
      throw new BusinessException(ErrorCode.INVALID_TOKEN);
    }
    return email;
  }

  // 토큰 삭제
  public void deleteToken(String token) {
    redisTemplate.delete(token);
  }

  // 토큰 존재 여부 (검증용)
  public boolean isValid(String token) {
    return redisTemplate.hasKey(token);
  }
}
