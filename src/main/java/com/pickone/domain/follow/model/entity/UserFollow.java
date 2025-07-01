package com.pickone.domain.follow.model.entity;

import com.pickone.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "user_follow",
    uniqueConstraints = @UniqueConstraint(columnNames = {"fromUserId", "toUserId"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserFollow extends BaseEntity {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long fromUserId;

  @Column(nullable = false)
  private Long toUserId;

  public static UserFollow of(Long fromUserId, Long toUserId) {
    return new UserFollow(null, fromUserId, toUserId);
  }
}
