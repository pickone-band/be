package com.pickone.global.security.repository;

import org.springframework.stereotype.Repository;

public interface TokenBlacklistRepository {
  void addToBlacklist(String token, long ttlMillis);
  boolean isBlacklisted(String token);
}
