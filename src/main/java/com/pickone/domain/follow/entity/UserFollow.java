package com.pickone.domain.follow.entity;

import com.pickone.domain.user.entity.User;
import com.pickone.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "user_follow",
    uniqueConstraints = @UniqueConstraint(columnNames = {"from_user_id", "to_user_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserFollow extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "from_user_id", nullable = false)
  private User fromUser;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "to_user_id", nullable = false)
  private User toUser;

  public static UserFollow create(User fromUser, User toUser) {
    return new UserFollow(null, fromUser, toUser);
  }
}
