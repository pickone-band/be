package com.pickone.domain.userInstrument.entity;

import com.pickone.domain.user.entity.User;
import com.pickone.global.common.enums.Instrument;
import com.pickone.global.common.enums.Proficiency;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_instruments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserInstrument {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Instrument instrument;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Proficiency proficiency;

  public void setUser(User user) {
    this.user = user;
  }

  public static UserInstrument create(User user, Instrument instrument, Proficiency proficiency) {
    return new UserInstrument(null, user, instrument, proficiency);
  }
}
