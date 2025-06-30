package com.pickone.domain.user.dto;

import com.pickone.domain.consent.dto.ConsentTermtDto;
import com.pickone.domain.user.model.domain.Gender;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.global.common.enums.Genre;
import com.pickone.global.common.enums.Mbti;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public record SignupRequestDto(

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "유효한 이메일 형식이 아닙니다")
    String email,

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다")
    String password,

    @NotBlank(message = "닉네임은 필수입니다")
    String nickname,

    @NotNull(message = "생년월일은 필수입니다")
    LocalDate birthDate,

    @NotNull(message = "성별은 필수입니다")
    Gender gender,

    @NotBlank(message = "전화번호는 필수입니다")
    String phoneNumber,

    // 선택 항목
    List<InstrumentInfoDto> instruments,
    List<Genre> genres,
    Mbti mbti,

    // 필수 항목
    @NotEmpty(message = "약관 동의 내역은 필수입니다")
    List<ConsentTermtDto> agreements

) {

    public UserEntity toEntity(String encodedPassword) {

        List<Genre> genreEnums = genres == null ? List.of() : genres;

        return UserEntity.of(
            email,
            encodedPassword,
            nickname,
            gender,
            birthDate,
            mbti,
            genreEnums
        );
    }

}