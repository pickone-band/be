package com.PickOne.domain.recruitments.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.PickOne.domain.recruitments.dto.request.InstrumentProficiencyDto;
import com.PickOne.domain.recruitments.dto.request.GenreRequestDto;
import com.PickOne.domain.recruitments.dto.request.RecruitmentRequestDto;
import com.PickOne.domain.recruitments.dto.response.RecruitmentResponseDto;
import com.PickOne.domain.recruitments.model.Genre;
import com.PickOne.domain.recruitments.model.Instrument;
import com.PickOne.domain.recruitments.model.Proficiency;
import com.PickOne.domain.recruitments.model.Status;
import com.PickOne.domain.recruitments.model.Type;
import com.PickOne.domain.recruitments.model.Visibility;
import com.PickOne.domain.recruitments.repository.RecruitmentGenreRepository;
import com.PickOne.domain.recruitments.repository.RecruitmentInstrumentRepository;
import com.PickOne.domain.recruitments.repository.RecruitmentRepository;
import com.PickOne.test.TestConfig;
import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestConfig.class)
@SpringBootTest
@Transactional
class RecruitmentServiceTest {

    @Autowired
    private RecruitmentService recruitmentService;
    @Autowired
    private RecruitmentRepository recruitmentRepository;
    @Autowired
    private RecruitmentInstrumentRepository recruitmentInstrumentRepository;
    @Autowired
    private RecruitmentGenreRepository recruitmentGenreRepository;

    RecruitmentRequestDto requestDto = RecruitmentRequestDto.builder()
            .type(Type.Once)
            .status(Status.Recruiting)
            .visibility(Visibility.PUBLIC)
            .title("테스트 모집공고1")
            .description("우리는 주말마다 연습하는 인디 밴드입니다.")
            .region("서울")
            .thumbnail("https://www.schezade.co.kr/board/guide/upload/guide_45_3.jpg")
            .snsLink("인스타주소")
            .instrumentProficiencyDto(List.of(
                    new InstrumentProficiencyDto(List.of(
                            InstrumentProficiencyDto.InstrumentDetail.builder()
                                    .instrument(Instrument.ELECTRIC_GUITAR)
                                    .proficiency(Proficiency.ADVANCED)
                                    .build(),
                            InstrumentProficiencyDto.InstrumentDetail.builder()
                                    .instrument(Instrument.BASS)
                                    .proficiency(Proficiency.INTERMEDIATE)
                                    .build()
                    ))
            ))
            .genreRequestDto(
                    new GenreRequestDto(
                            List.of(
                                    Genre.INDIE_ROCK,
                                    Genre.SHOEGAZING
                            )
                    )
            )
            .build();

    @Test
    void 모집공고_등록_성공_테스트() {
        //when
        Long savedId = recruitmentService.registerRecruitment(requestDto);

        // then
        assertNotNull(savedId);
        System.out.println("등록된 ID: " + savedId);
    }

    @Test
    void 모집공고_단건_조회_테스트() {
        recruitmentService.registerRecruitment(requestDto);
        RecruitmentResponseDto responseDto = recruitmentService.getRecruitment(1L);
        assertEquals(responseDto.getGenres().getGenre(), List.of(Genre.INDIE_ROCK, Genre.SHOEGAZING));
    }
}