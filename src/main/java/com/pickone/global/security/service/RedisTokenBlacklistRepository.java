package com.pickone.global.security.service;

import com.pickone.global.security.repository.TokenBlacklistRepository;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisTokenBlacklistRepository implements TokenBlacklistRepository {
  private final StringRedisTemplate redisTemplate;
  private static final String PREFIX = "blacklist:";

  @Override
  public void addToBlacklist(String token, long ttlMillis) {
    redisTemplate.opsForValue().set(PREFIX + token, "1", Duration.ofMillis(ttlMillis));
  }

  @Override
  public boolean isBlacklisted(String token) {
    return redisTemplate.hasKey(PREFIX + token);
  }
}
