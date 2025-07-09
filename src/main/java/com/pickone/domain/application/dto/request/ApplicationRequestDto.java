package com.pickone.domain.application.dto.request;

import com.pickone.domain.application.model.entity.Application;
import com.pickone.domain.recruitments.model.entity.Recruitment;
import com.pickone.domain.user.entity.User;
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
public class ApplicationRequestDto {
    private String message;
    private String portfolioUrl;
    private String thumbnail;

    ApplicationInstrumentDto appInstrument;

    private Mbti mbti;
    private Instrument instrument;
    private Proficiency proficiency;

    public Application toEntity(User user, Recruitment recruitment) {
        return Application.builder()
                .message(message)
                .portfolioUrl(portfolioUrl)
                .thumbnail(thumbnail)
                .mbti(mbti)
                .instrument(instrument)
                .proficiency(proficiency)
                .user(user)
                .recruitment(recruitment)
                .build();
    }
}
