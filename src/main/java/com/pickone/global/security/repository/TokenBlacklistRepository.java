package com.pickone.global.security.repository;

public interface TokenBlacklistRepository {
  void addToBlacklist(String token, long ttlMillis);
  boolean isBlacklisted(String token);
}
