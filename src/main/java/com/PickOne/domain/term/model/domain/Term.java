package com.PickOne.domain.term.model.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * 약관(Term) 도메인 객체. 제목, 내용, 버전, 필수 여부, 시행일 등의 정보를 포함한다.
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class Term {
    private final Long id;
    private final String title;
    private final String content;
    private final String version;
    private final boolean required;
    private final LocalDateTime effectiveDate;
}