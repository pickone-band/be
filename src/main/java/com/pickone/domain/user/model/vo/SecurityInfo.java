package com.pickone.domain.user.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SecurityInfo {

  @Column(nullable = false)
  private boolean isOauth;

  @Column(nullable = false)
  private boolean isVerified;

  private LocalDateTime credentialsExpiredAt;

  public boolean isCredentialExpired() {
    return credentialsExpiredAt != null && credentialsExpiredAt.isBefore(LocalDateTime.now());
  }

  public SecurityInfo verify() {
    return new SecurityInfo(isOauth, true, credentialsExpiredAt);
  }
}
