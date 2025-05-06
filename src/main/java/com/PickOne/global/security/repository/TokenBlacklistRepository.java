package com.PickOne.global.security.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface TokenBlacklistRepository {
    void addToBlacklist(String token, long ttlMillis);
    boolean isBlacklisted(String token);
}
