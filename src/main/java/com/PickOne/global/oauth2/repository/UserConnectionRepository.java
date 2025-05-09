package com.PickOne.global.oauth2.repository;

import com.PickOne.domain.user.model.entity.UserEntity;
import com.PickOne.global.oauth2.model.domain.OAuth2Provider;
import com.PickOne.global.oauth2.model.entity.UserConnectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserConnectionRepository extends JpaRepository<UserConnectionEntity, Long> {
    Optional<UserConnectionEntity> findByProviderAndProviderId(OAuth2Provider provider, String providerId);
    List<UserConnectionEntity> findByUser(UserEntity user);
    List<UserConnectionEntity> findByUserId(Long userId);
    Optional<UserConnectionEntity> findByUserIdAndProvider(Long userId, OAuth2Provider provider);
    boolean existsByProviderAndProviderId(OAuth2Provider provider, String providerId);
}