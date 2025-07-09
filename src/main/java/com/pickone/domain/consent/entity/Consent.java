package com.pickone.domain.consent.entity;

import com.pickone.domain.term.entity.Term;

import com.pickone.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "consent", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "term_id"})
})
public class Consent {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "term_id", nullable = false)
  private Term term;

  @Column(nullable = false)
  private Boolean consented;

  @Column(nullable = false)
  private LocalDateTime consentedAt;

  public static Consent create(User user, Term term, boolean consented, LocalDateTime consentedAt) {
    return new Consent(null, user, term, consented, consentedAt);
  }
}