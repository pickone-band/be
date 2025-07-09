package com.pickone.domain.follow.repository;

import com.pickone.domain.follow.entity.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserFollowJpaRepository extends JpaRepository<UserFollow, Long> {
  Optional<UserFollow> findByFromUserIdAndToUserId(Long fromUserId, Long toUserId);
  List<UserFollow> findByFromUserId(Long fromUserId);
  List<UserFollow> findByToUserId(Long toUserId);
  boolean existsByFromUserIdAndToUserId(Long fromUserId, Long toUserId);
  void deleteByFromUserIdAndToUserId(Long fromUserId, Long toUserId);
}
