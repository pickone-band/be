package com.pickone.domain.application.dto.request;

import com.pickone.domain.application.model.entity.Application;
import com.pickone.global.common.enums.Instrument;
import com.pickone.global.common.enums.Mbti;
import com.pickone.global.common.enums.Proficiency;
import com.pickone.domain.recruitments.model.entity.Recruitment;
import com.pickone.domain.user.model.entity.UserEntity;
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
