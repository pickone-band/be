package com.pickone.domain.user.model.entity;

import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.global.common.enums.Instrument;
import com.pickone.global.common.enums.Proficiency;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_instruments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserInstrumentEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Instrument instrument;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Proficiency proficiency;

  public void setUser(UserEntity user) {
    this.user = user;
  }

  public static UserInstrumentEntity of(UserEntity user, Instrument instrument, Proficiency proficiency) {
    return new UserInstrumentEntity(null, user, instrument, proficiency);
  }
}
