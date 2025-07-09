package com.pickone.domain.userMusic;

import com.pickone.domain.user.entity.User;
import com.pickone.global.common.enums.Genre;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserMusic {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Genre genre;

  public static UserMusic create(User user, Genre genre) {
    return new UserMusic(null, user, genre);
  }
}
