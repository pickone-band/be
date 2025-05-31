package com.PickOne.global.oauth2.repository;

import com.PickOne.global.oauth2.model.entity.UserConnectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserConnectionRepository extends JpaRepository<UserConnectionEntity, Long> {
    Optional<UserConnectionEntity> findByProviderAndProviderUserId(String provider, String providerUserId);
}