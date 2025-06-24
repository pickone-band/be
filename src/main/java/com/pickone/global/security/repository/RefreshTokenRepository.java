package com.pickone.global.security.repository;

public interface RefreshTokenRepository {

  void save(String email, String refreshToken, long expirationMillis);

  String find(String email);

  void delete(String email);
}
