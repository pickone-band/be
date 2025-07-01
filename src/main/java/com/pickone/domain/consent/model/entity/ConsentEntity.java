package com.pickone.domain.consent.model.entity;

import com.pickone.domain.term.model.entity.TermEntity;
import com.pickone.domain.user.model.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "consent",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "term_id"})
)
public class ConsentEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "term_id", nullable = false)
  private TermEntity term;

  @Column(nullable = false)
  private Boolean consented;

  @Column(nullable = false)
  private LocalDateTime consentedAt;

  public static ConsentEntity of(UserEntity user, TermEntity term, Boolean consented, LocalDateTime consentedAt) {
    return new ConsentEntity(null, user, term, consented, consentedAt);
  }
}
