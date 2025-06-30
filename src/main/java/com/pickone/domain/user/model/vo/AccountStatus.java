package com.pickone.domain.user.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatus {

  @Column(nullable = false)
  private boolean isActive;

  @Column(nullable = false)
  private boolean isLocked;

  public boolean canLogin() {
    return isActive && !isLocked;
  }

  public AccountStatus activate() {
    return new AccountStatus(true, this.isLocked);
  }

  public AccountStatus lock() {
    return new AccountStatus(this.isActive, true);
  }

  public AccountStatus unlock() {
    return new AccountStatus(this.isActive, false);
  }

  public static AccountStatus active() {
    return new AccountStatus(true, false);
  }
}