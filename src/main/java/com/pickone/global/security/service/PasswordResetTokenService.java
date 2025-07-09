package com.pickone.global.security.service;

import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetTokenService {

  private final StringRedisTemplate redis;
  private static final long EXPIRE_SECONDS = 3600; // 1시간

  public String createAndSaveToken(String email) {
    String token = UUID.randomUUID().toString();
    redis.opsForValue().set(token, email, EXPIRE_SECONDS, TimeUnit.SECONDS);
    log.debug("[PasswordResetTokenService] 생성 완료: email={}, token={}", email, token);
    return token;
  }

  public String validateTokenAndGetEmail(String token) {
    String email = redis.opsForValue().get(token);
    if (email == null) {
      log.warn("[PasswordResetTokenService] 유효하지 않은 토큰 요청됨: {}", token);
      throw new BusinessException(ErrorCode.INVALID_TOKEN);
    }
    return email;
  }

  public boolean isValid(String token) {
    return redis.hasKey(token);
  }

  public void deleteToken(String token) {
    redis.delete(token);
    log.debug("[PasswordResetTokenService] 삭제됨: token={}", token);
  }
}
