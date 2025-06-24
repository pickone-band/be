package com.pickone.global.common.enums;

import lombok.Getter;

@Getter
public enum Genre {
    // 락 계열
    INDIE_ROCK("Indie Rock"),               // 인디 락
    ALTERNATIVE_ROCK("Alternative Rock"),   // 얼터너티브 락
    HARD_ROCK("Hard Rock"),                 // 하드 락
    POST_ROCK("Post Rock"),                 // 포스트 락
    SHOEGAZING("Shoegazing"),               // 슈게이징
    HEAVY_METAL("Heavy Metal"),             // 헤비 메탈
    PUNK_ROCK("Punk Rock"),                 // 펑크 락
    GRUNGE("Grunge"),                       // 그런지
    PROGRESSIVE_ROCK("Progressive Rock"),   // 프로그레시브 락
    GARAGE_ROCK("Garage Rock"),             // 개러지 락
    CLASSIC_ROCK("Classic Rock"),           // 클래식 락

    // 메탈 계열
    DEATH_METAL("Death Metal"),             // 데스 메탈
    BLACK_METAL("Black Metal"),             // 블랙 메탈
    THRASH_METAL("Thrash Metal"),           // 쓰래쉬 메탈
    DOOM_METAL("Doom Metal"),               // 둠 메탈

    // 포크 및 어쿠스틱
    FOLK("Folk"),                           // 포크
    FOLK_ROCK("Folk Rock"),                 // 포크 락
    ACOUSTIC("Acoustic"),                   // 어쿠스틱

    // 재즈 계열
    JAZZ("Jazz"),                           // 재즈
    SMOOTH_JAZZ("Smooth Jazz"),             // 스무스 재즈
    FUSION_JAZZ("Fusion Jazz"),             // 퓨전 재즈
    LOFI_JAZZ("Lofi Jazz"),                 // 로파이 재즈

    // 팝 계열
    POP("Pop"),                             // 팝
    DREAM_POP("Dream Pop"),                 // 드림 팝
    SYNTH_POP("Synth Pop"),                 // 신스 팝
    ELECTRO_POP("Electro Pop"),             // 일렉트로 팝

    // 힙합 및 일렉트로닉
    HIPHOP("Hip-Hop"),                      // 힙합
    LOFI("Lo-fi"),                          // 로파이
    EDM("EDM"),                             // EDM
    AMBIENT("Ambient"),                     // 앰비언트
    HOUSE("House"),                         // 하우스
    TECHNO("Techno"),                       // 테크노
    TRANCE("Trance"),                       // 트랜스

    // 블루스 및 소울
    BLUES("Blues"),                         // 블루스
    SOUL("Soul"),                           // 소울
    RNB("R&B"),                             // R&B

    // 기타
    CLASSICAL("Classical"),                 // 클래식
    FUNK("Funk"),                           // 펑크
    REGGAE("Reggae"),                       // 레게
    WORLD_MUSIC("World Music"),             // 월드 뮤직
    EXPERIMENTAL("Experimental"),           // 실험 음악
    POST_PUNK("Post Punk"),                 // 포스트 펑크
    MATH_ROCK("Math Rock"),                 // 매스 락
    GOSPEL("Gospel");                       // 가스펠

    private final String displayName;

    Genre(String displayName) {
        this.displayName = displayName;
    }
}
