package com.PickOne.domain.recruitments.dto.request;

import com.PickOne.domain.recruitments.model.Genre;
import com.PickOne.domain.recruitments.model.Instrument;
import com.PickOne.domain.recruitments.model.Status;
import com.PickOne.domain.recruitments.model.Type;
import com.PickOne.domain.recruitments.model.Visibility;
import com.PickOne.domain.recruitments.model.entity.Recruitment;
import com.PickOne.domain.user.model.entity.UserEntity;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruitmentRequestDto {
    private Type type;                  // 모집 타입
    private Status status;              // 모집 상태
    private Visibility visibility;
    private String title;                // 제목
    private String description;          // 상세 설명
    private String region;               // 지역
    private String thumbnail;            // 이미지 URL
    private String snsLink;              // sns 링크

    private List<InstrumentProficiencyDto> instrumentProficiencyDto;
    private GenreRequestDto genreRequestDto;

    public Recruitment toEntity(UserEntity userEntity) {
        return Recruitment.builder()
                .type(type)
                .status(status)
                .visibility(visibility)
                .title(title)
                .description(description)
                .region(region)
                .thumbnail(thumbnail)
                .snsLink(snsLink)
                .userEntity(userEntity)
                .build();
    }
}
