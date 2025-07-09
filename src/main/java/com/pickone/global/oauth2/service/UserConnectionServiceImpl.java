package com.pickone.global.oauth2.service;

import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import com.pickone.global.oauth2.entity.UserConnection;
import com.pickone.global.oauth2.model.domain.OAuth2Provider;
import com.pickone.global.oauth2.repository.UserConnectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserConnectionServiceImpl implements UserConnectionService {

  private final UserConnectionRepository repository;

  @Override
  @Transactional
  public UserConnection findOrCreateConnection(OAuth2Provider provider, String providerUserId,
      String email, String nickname, Long userId) {

    return repository.findByProviderAndProviderUserId(provider, providerUserId)
        .orElseGet(() -> {
          UserConnection connection = UserConnection.ofFull(
              provider,
              providerUserId,
              email,
              nickname,
              null,
              null,
              userId
          );
          log.info("[UserConnectionService] 새로운 연결 생성: provider={}, providerUserId={}", provider, providerUserId);
          return repository.save(connection);
        });
  }

  @Override
  @Transactional
  public void updateTokens(Long userId, OAuth2Provider provider, String accessToken, String refreshToken) {
    UserConnection connection = repository.findByProviderAndUserId(provider, userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.SOCIAL_ACCOUNT_NOT_FOUND));
    connection.updateTokens(accessToken, refreshToken);
    log.info("[UserConnectionService] 토큰 갱신 완료: provider={}, userId={}", provider, userId);
  }

  @Override
  @Transactional(readOnly = true)
  public UserConnection getConnectionByUserIdAndProvider(Long userId, OAuth2Provider provider) {
    return repository.findByProviderAndUserId(provider, userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.SOCIAL_ACCOUNT_NOT_FOUND));
  }
}
