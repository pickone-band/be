package com.pickone.global.oauth2.service;

import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import com.pickone.global.oauth2.entity.UserConnection;
import com.pickone.global.oauth2.model.domain.OAuth2Provider;
import com.pickone.global.oauth2.repository.UserConnectionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserConnectionServiceImplTest {

  private UserConnectionServiceImpl service;
  private UserConnectionRepository repository;

  @BeforeEach
  void setUp() {
    repository = mock(UserConnectionRepository.class);
    service = new UserConnectionServiceImpl(repository);
  }

  @Test
  void findOrCreateConnection_shouldReturnExistingConnection() {
    // given
    OAuth2Provider provider = OAuth2Provider.SPOTIFY;
    String providerUserId = "abc123";
    String email = "test@example.com";
    String nickname = "tester";
    Long userId = 1L;

    UserConnection existing = mock(UserConnection.class);
    when(repository.findByProviderAndProviderUserId(provider, providerUserId))
        .thenReturn(Optional.of(existing));

    // when
    UserConnection result = service.findOrCreateConnection(provider, providerUserId, email, nickname, userId);

    // then
    assertEquals(existing, result);
    verify(repository, never()).save(any());
  }

  @Test
  void findOrCreateConnection_shouldCreateNewConnectionIfNotExist() {
    // given
    OAuth2Provider provider = OAuth2Provider.SPOTIFY;
    String providerUserId = "new123";
    String email = "new@example.com";
    String nickname = "newbie";
    Long userId = 2L;

    when(repository.findByProviderAndProviderUserId(provider, providerUserId))
        .thenReturn(Optional.empty());

    // mock 저장되는 객체
    UserConnection toSave = UserConnection.ofFull(provider, providerUserId, email, nickname, null, null, userId);
    when(repository.save(any(UserConnection.class))).thenReturn(toSave);

    // when
    UserConnection result = service.findOrCreateConnection(provider, providerUserId, email, nickname, userId);

    // then
    assertNotNull(result);
    assertEquals(providerUserId, result.getProviderUserId());
    verify(repository).save(any(UserConnection.class));
  }

  @Test
  void updateTokens_shouldUpdateTokensIfExists() {
    // given
    OAuth2Provider provider = OAuth2Provider.INSTAGRAM;
    Long userId = 5L;
    String accessToken = "access";
    String refreshToken = "refresh";

    UserConnection connection = mock(UserConnection.class);
    when(repository.findByProviderAndUserId(provider, userId))
        .thenReturn(Optional.of(connection));

    // when
    service.updateTokens(userId, provider, accessToken, refreshToken);

    // then
    verify(connection).updateTokens(accessToken, refreshToken);
  }

  @Test
  void updateTokens_shouldThrowExceptionIfNotFound() {
    // given
    OAuth2Provider provider = OAuth2Provider.SPOTIFY;
    Long userId = 999L;

    when(repository.findByProviderAndUserId(provider, userId)).thenReturn(Optional.empty());

    // expect
    BusinessException ex = assertThrows(BusinessException.class, () ->
        service.updateTokens(userId, provider, "access", "refresh"));

    assertEquals(ErrorCode.SOCIAL_ACCOUNT_NOT_FOUND, ex.getErrorCode());
  }

  @Test
  void getConnectionByUserIdAndProvider_shouldReturnConnection() {
    OAuth2Provider provider = OAuth2Provider.SPOTIFY;
    Long userId = 10L;
    UserConnection connection = mock(UserConnection.class);

    when(repository.findByProviderAndUserId(provider, userId))
        .thenReturn(Optional.of(connection));

    UserConnection result = service.getConnectionByUserIdAndProvider(userId, provider);

    assertEquals(connection, result);
  }

  @Test
  void getConnectionByUserIdAndProvider_shouldThrowIfNotFound() {
    OAuth2Provider provider = OAuth2Provider.SPOTIFY;
    Long userId = 10L;

    when(repository.findByProviderAndUserId(provider, userId))
        .thenReturn(Optional.empty());

    BusinessException ex = assertThrows(BusinessException.class, () ->
        service.getConnectionByUserIdAndProvider(userId, provider));

    assertEquals(ErrorCode.SOCIAL_ACCOUNT_NOT_FOUND, ex.getErrorCode());
  }
}
