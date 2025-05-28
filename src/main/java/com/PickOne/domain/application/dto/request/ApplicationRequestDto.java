package com.PickOne.domain.application.dto.request;

import com.PickOne.domain.application.model.entity.Application;
import com.PickOne.domain.recruitments.model.Instrument;
import com.PickOne.domain.recruitments.model.Mbti;
import com.PickOne.domain.recruitments.model.Proficiency;
import com.PickOne.domain.recruitments.model.entity.Recruitment;
import com.PickOne.domain.user.model.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationRequestDto {
    private String message;
    private String portfolioUrl;
    private String thumbnail;

    ApplicationInstrumentDto appInstrument;

    private Mbti mbti;
    private Instrument instrument;
    private Proficiency proficiency;

    public Application toEntity(UserEntity userEntity, Recruitment recruitment) {
        return Application.builder()
                .message(message)
                .portfolioUrl(portfolioUrl)
                .thumbnail(thumbnail)
                .mbti(mbti)
                .instrument(instrument)
                .proficiency(proficiency)
                .userEntity(userEntity)
                .recruitment(recruitment)
                .build();
    }
}
