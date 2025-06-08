package com.PickOne.domain.user.model.domain;

public enum Role {

    USER, ADMIN;

    public boolean isAdmin() {
        return this == ADMIN;
    }
}