package com.pickone.domain.follow.model.entity;

import com.pickone.domain.user.model.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_follow")
public class UserFollow {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "follower_id", nullable = false)
  private UserEntity follower; // 팔로우를 거는 사람

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "following_id", nullable = false)
  private UserEntity following; // 팔로우 당하는 사람

  @Builder
  public UserFollow(UserEntity follower, UserEntity following) {
    this.follower = follower;
    this.following = following;
  }
}