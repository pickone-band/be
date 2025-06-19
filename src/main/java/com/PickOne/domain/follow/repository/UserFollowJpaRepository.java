package com.PickOne.domain.follow.repository;

import com.PickOne.domain.follow.model.entity.UserFollow;
import com.PickOne.domain.user.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserFollowJpaRepository extends JpaRepository<UserFollow, Long> {
    boolean existsByFollowerAndFollowing(UserEntity follower, UserEntity following);
    Optional<UserFollow> findByFollowerAndFollowing(UserEntity follower, UserEntity following);
    List<UserFollow> findAllByFollower(UserEntity follower);
    List<UserFollow> findAllByFollowing(UserEntity following);
}
