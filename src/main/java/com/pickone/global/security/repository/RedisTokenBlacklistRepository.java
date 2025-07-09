package com.pickone.global.security.service;

import com.pickone.global.security.repository.TokenBlacklistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisTokenBlacklistRepository implements TokenBlacklistRepository {

  private final StringRedisTemplate redis;
  private static final String PREFIX = "blacklist:";

  @Override
  public void addToBlacklist(String token, long ttlMillis) {
    redis.opsForValue().set(PREFIX + token, "1", Duration.ofMillis(ttlMillis));
    log.debug("[RedisTokenBlacklistRepo] 블랙리스트 등록됨: ttl={}ms", ttlMillis);
  }

  @Override
  public boolean isBlacklisted(String token) {
    return redis.hasKey(PREFIX + token);
  }
}
