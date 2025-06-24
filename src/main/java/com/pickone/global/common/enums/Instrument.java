package com.pickone.global.common.enums;

import lombok.Getter;

@Getter
public enum Instrument {
    // 기타 종류
    ACOUSTIC_GUITAR("Acoustic Guitar"), // 통기타
    ELECTRIC_GUITAR("Electric Guitar"), // 일렉기타
    BASS("Bass"),                       // 베이스

    // 건반 악기
    KEYBOARD("Keyboard"),               // 키보드
    SYNTHESIZER("Synthesizer"),         // 신디사이저

    // 타악기
    DRUMS("Drums"),                     // 드럼
    PERCUSSION("Percussion"),           // 타악기

    // 현악기
    VIOLIN("Violin"),                   // 바이올린
    CELLO("Cello"),                     // 첼로

    // 관악기
    SAXOPHONE("Saxophone"),             // 색소폰
    TRUMPET("Trumpet"),                 // 트럼펫
    FLUTE("Flute"),                     // 플루트

    // 보컬
    VOCALS("Vocals"),                   // 보컬
    BACKING_VOCALS("Backing Vocals");   // 코러스

    private final String displayName;

    Instrument(String displayName) {
        this.displayName = displayName;
    }
}