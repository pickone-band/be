package com.pickone.domain.user.model.vo;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserStatus {

  @Column(name = "is_public", nullable = false)
  private boolean isPublic;

  @Column(name = "is_active", nullable = false)
  private boolean active;

  @Column(name = "is_locked", nullable = false)
  private boolean locked;

  @Column(name = "is_verified", nullable = false)
  private boolean verified;

  @Column(name = "credentials_expired_at")
  private LocalDateTime credentialsExpiredAt;

  public static UserStatus init() {
    return new UserStatus(false, true, false, false, null);
  }

  public UserStatus verify() {
    return new UserStatus(this.isPublic, this.active, this.locked, true, this.credentialsExpiredAt);
  }

  public UserStatus lock() {
    return new UserStatus(this.isPublic, this.active, true, this.verified, this.credentialsExpiredAt);
  }
}
