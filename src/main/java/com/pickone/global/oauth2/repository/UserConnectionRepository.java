package com.pickone.global.oauth2.repository;

import com.pickone.global.oauth2.entity.UserConnection;
import com.pickone.global.oauth2.model.domain.OAuth2Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserConnectionRepository extends JpaRepository<UserConnection, Long> {

  Optional<UserConnection> findByProviderAndProviderUserId(OAuth2Provider provider, String providerUserId);

  Optional<UserConnection> findByProviderAndUserId(OAuth2Provider provider, Long userId);

  boolean existsByProviderAndUserId(OAuth2Provider provider, Long userId);
}
