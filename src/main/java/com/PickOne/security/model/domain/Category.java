package com.PickOne.security.model.domain;

import lombok.Getter;

@Getter
public enum Category {

    // 추후에 추가(각 서비스 별로 권한 ex 밴드포탈, 회원
    POST("게시판");

    private final String value;

    Category(String value) {
        this.value = value;
    }
}
