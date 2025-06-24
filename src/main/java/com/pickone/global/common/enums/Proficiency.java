package com.pickone.global.common.enums;

import lombok.Getter;

@Getter
public enum Proficiency {

    NEVER_PLAYED("전혀 없음", "악기를 전혀 다뤄본 적이 없는 상태"),
    BEGINNER("초심자", "악기를 처음 접하거나 기본적인 연주도 어려운 상태"),
    BASIC("기본", "기본적인 코드와 멜로디 연주가 가능한 상태"),

    INTERMEDIATE("중급", "기본적인 곡 연주와 합주가 가능한 상태"),
    ADVANCED("고급", "다양한 연주 기법을 익히고 곡 해석이 가능한 상태"),
    SEMI_PRO("세미 프로", "음악 전공자는 아니지만 준전문가 수준의 실력을 가진 상태"),

    PROFESSIONAL("프로", "음악 전공자이거나 전문 연주 활동 경험이 있는 상태"),
    MASTER("마스터", "전문 연주자로 오랜 경력과 탁월한 실력을 가진 상태");

    private final String label; // 한글 명칭
    private final String description; // 설명

    Proficiency(String label, String description) {
        this.label = label;
        this.description = description;
    }
}

