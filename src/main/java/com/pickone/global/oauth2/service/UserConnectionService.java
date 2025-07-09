package com.pickone.global.oauth2.service;

import com.pickone.global.oauth2.model.domain.OAuth2Provider;
import com.pickone.global.oauth2.entity.UserConnection;

public interface UserConnectionService {

  UserConnection findOrCreateConnection(OAuth2Provider provider, String providerUserId, String email, String nickname, Long userId);

  void updateTokens(Long userId, OAuth2Provider provider, String accessToken, String refreshToken);

  UserConnection getConnectionByUserIdAndProvider(Long userId, OAuth2Provider provider);
}
