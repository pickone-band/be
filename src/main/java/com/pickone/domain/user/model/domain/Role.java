package com.pickone.domain.user.model.domain;

public enum Role {

  USER, ADMIN;

  public boolean isAdmin() {
    return this == ADMIN;
  }
}