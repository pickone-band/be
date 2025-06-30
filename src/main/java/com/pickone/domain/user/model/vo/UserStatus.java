package com.pickone.domain.user.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserStatus {

  @Column(name = "is_public", nullable = false)
  private boolean isPublic;

  @Column(name = "is_active", nullable = false)
  private boolean isActive;

  @Column(name = "is_locked", nullable = false)
  private boolean isLocked;

  @Column(name = "credentials_expired_at")
  private LocalDateTime credentialsExpiredAt;

  @Column(name = "is_verified", nullable = false)
  private boolean verified;

  public static UserStatus init() {
    return new UserStatus(false, true, false, null, false);
  }

  public UserStatus verify() {
    return new UserStatus(this.isPublic, this.isActive, this.isLocked, this.credentialsExpiredAt, true);
  }

  public UserStatus updatePublic(Boolean newPublic) {
    return new UserStatus(newPublic != null ? newPublic : this.isPublic, this.isActive, this.isLocked, this.credentialsExpiredAt, this.verified);
  }

  public UserStatus activate() {
    return new UserStatus(this.isPublic, true, this.isLocked, this.credentialsExpiredAt, this.verified);
  }

  public UserStatus lock() {
    return new UserStatus(this.isPublic, this.isActive, true, this.credentialsExpiredAt, this.verified);
  }

  public UserStatus unlock() {
    return new UserStatus(this.isPublic, this.isActive, false, this.credentialsExpiredAt, this.verified);
  }

  public UserStatus deactivate() {
    return new UserStatus(this.isPublic, false, this.isLocked, this.credentialsExpiredAt, this.verified);
  }

}
