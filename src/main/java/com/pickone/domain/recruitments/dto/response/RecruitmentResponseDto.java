package com.pickone.domain.recruitments.dto.response;

import com.pickone.domain.recruitments.model.Type;
import com.pickone.domain.recruitments.model.Visibility;
import com.pickone.domain.recruitments.model.entity.Recruitment;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecruitmentResponseDto {

    private Long id;                     // 모집공고 ID
    private Type type;                   // 모집 타입
    private Visibility visibility;
    private List<InstrumentResponseDto> instruments;
    private GenreResponseDto genres;
    private String title;                 // 제목
    private String description;           // 상세 설명
    private String region;                // 지역
    private String thumbnail;                 // 이미지 URL
    private String snsLink;
    private String createdAt;             // 생성일

    public static RecruitmentResponseDto of(Recruitment r, List<InstrumentResponseDto> instruments, GenreResponseDto genreResponseDtos) {
        return RecruitmentResponseDto.builder()
                .id(r.getId())
                .type(r.getType())
                .visibility(r.getVisibility())
                .instruments(instruments)
                .genres(genreResponseDtos)
                .title(r.getTitle())
                .description(r.getDescription())
                .region(r.getRegion())
                .snsLink(r.getSnsLink())
                .thumbnail(r.getThumbnail())
                .build();
    }
}