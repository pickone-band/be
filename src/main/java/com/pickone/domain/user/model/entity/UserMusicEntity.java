package com.pickone.domain.user.model.entity;

import com.pickone.global.common.enums.Genre;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserMusicEntity {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Genre genre;

  public static UserMusicEntity of(UserEntity user, Genre genre) {
    return new UserMusicEntity(null, user, genre);
  }
}
