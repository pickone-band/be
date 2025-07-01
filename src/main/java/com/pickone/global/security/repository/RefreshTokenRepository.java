package com.pickone.global.security.repository;

public interface RefreshTokenRepository {
  void save(String userEmail, String refreshToken, long ttlMillis);
  boolean existsByToken(String refreshToken);
  void deleteByToken(String refreshToken);
  String findUserEmailByToken(String refreshToken);
}
