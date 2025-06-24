package com.pickone.domain.application.dto.response;

import com.pickone.domain.application.model.ApplicationStatus;
import com.pickone.global.common.enums.Instrument;
import com.pickone.global.common.enums.Mbti;
import com.pickone.global.common.enums.Proficiency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationResponseDto {
    private Long recruitmentId;             // 모집공고 ID
    private String title;                   // 모집공고 제목
    private String region;                  // 모집공고 지역
    private String thumbnail;               // 모집공고 썸네일
    private String createdAt;               // 모집공고 생성일

    // 내가 신청한 정보
    private String message;                 // 지원 메시지
    private String portfolioUrl;            // 포트폴리오 링크
    private String applicantThumbnail;      // 내 썸네일
    private Mbti mbti;                      // MBTI
    private Instrument instrument;          // 악기
    private Proficiency proficiency;        // 능숙도
    private ApplicationStatus status;       // 지원현황
}