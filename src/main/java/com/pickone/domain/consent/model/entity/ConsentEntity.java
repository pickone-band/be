package com.pickone.domain.consent.model.entity;

import com.pickone.domain.term.model.entity.TermEntity;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "consents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ConsentEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "users_id", nullable = false)
  private UserEntity user;

  public void setUser(UserEntity user) {
    this.user = user;
  }

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "terms_id", nullable = false)
  private TermEntity term;

  @Column(nullable = false)
  private boolean consented;

  @Column(nullable = false)
  private LocalDateTime consentDate;

  @Builder
  private ConsentEntity(
      UserEntity user,
      TermEntity term,
      boolean consented,
      LocalDateTime consentDate
  ) {
    this.user = user;
    this.term = term;
    this.consented = consented;
    this.consentDate = consentDate;
  }
}
