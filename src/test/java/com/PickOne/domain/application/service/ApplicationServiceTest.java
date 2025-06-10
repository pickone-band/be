package com.PickOne.domain.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.PickOne.domain.application.dto.request.ApplicationRequestDto;
import com.PickOne.domain.application.dto.request.ApplicationResponseDto;
import com.PickOne.domain.recruitments.model.Instrument;
import com.PickOne.domain.recruitments.model.Mbti;
import com.PickOne.domain.recruitments.model.Proficiency;
import com.PickOne.domain.recruitments.model.entity.Recruitment;
import com.PickOne.domain.recruitments.repository.RecruitmentRepository;
import com.PickOne.domain.user.mapper.UserMapper;
import com.PickOne.domain.user.model.domain.*;
import com.PickOne.domain.user.model.entity.UserEntity;
import com.PickOne.domain.user.repository.UserRepository;
import com.PickOne.global.security.repository.AuthRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import java.util.List;

@SpringBootTest
public class ApplicationServiceTest {

    @MockBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @MockBean
    private OAuth2AuthorizedClientService oAuth2AuthorizedClientService;


    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private AuthRepository authRepository;
    @Autowired
    private RecruitmentRepository recruitmentRepository;

    @Test
    void 멤버_지원_테스트() {
        // given
        User testUser = new User(
                null,
                Email.of("test@example.com"),
                Password.ofEncoded("encoded-password"),
                new Nickname("테스트유저"),
                new ProfileImage("https://example.com/profile.jpg"),
                true,   // isPublic
                false,  // isVerified
                false,  // isOauth
                Role.USER,
                List.of(new com.PickOne.domain.user.model.domain.Instrument("Guitar")),
                List.of(new com.PickOne.domain.user.model.domain.Genre("Rock"))
        );

        User savedUser = authRepository.save(testUser);

        Recruitment recruitment = Recruitment.builder()
                .title("테스트 모집글")
                .userEntity(UserMapper.toEntity(savedUser))
                .build();
        recruitmentRepository.save(recruitment);

        ApplicationRequestDto requestDto = ApplicationRequestDto.builder()
                .message("열정적으로 연주하고 싶습니다!")
                .portfolioUrl("https://www.youtube.com/watch?v=abc123")
                .thumbnail("https://example.com/image.jpg")
                .mbti(Mbti.ENFP)
                .instrument(Instrument.ELECTRIC_GUITAR)
                .proficiency(Proficiency.ADVANCED)
                .build();

        // when
        Long applicationId = applicationService.applyToRecruitment(savedUser.getId(), recruitment.getId(), requestDto);

        // then
        assertNotNull(applicationId);
        System.out.println("지원 ID: " + applicationId);
    }

    @Test
    void 멤버_지원_조회_테스트() {
        // given
        User testUser = User.of(
                null,
                Email.of("test2@example.com"),
                Password.ofEncoded("encoded-password"),
                "테스트유저2",
                true
        );
        User savedUser = userRepository.save(testUser);

        // 모집글 등록
        Recruitment recruitment = Recruitment.builder()
                .title("조회용 모집글")
                .userEntity(UserMapper.toEntity(savedUser))
                .build();
        recruitmentRepository.save(recruitment);

        // 지원 요청
        ApplicationRequestDto requestDto = ApplicationRequestDto.builder()
                .message("조회 테스트 지원")
                .portfolioUrl("https://portfolio.com")
                .thumbnail("https://img.com/test.jpg")
                .mbti(Mbti.INFP)
                .instrument(Instrument.DRUMS)
                .proficiency(Proficiency.INTERMEDIATE)
                .build();

        // when - 지원
        Long applicationId = applicationService.applyToRecruitment(savedUser.getId(), recruitment.getId(), requestDto);
        assertNotNull(applicationId);

        // when - 조회
        ApplicationResponseDto application = applicationService.getMyApplication(savedUser.getId(), recruitment.getId());

        // then
        assertNotNull(application);
       assertEquals(application.getPortfolioUrl(),requestDto.getPortfolioUrl());
    }
}
