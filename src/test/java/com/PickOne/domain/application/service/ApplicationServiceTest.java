package com.PickOne.domain.application.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.PickOne.domain.application.dto.request.ApplicationRequestDto;
import com.PickOne.domain.recruitments.model.Instrument;
import com.PickOne.domain.recruitments.model.Mbti;
import com.PickOne.domain.recruitments.model.Proficiency;
import com.PickOne.domain.recruitments.model.entity.Recruitment;
import com.PickOne.domain.recruitments.repository.RecruitmentRepository;
import com.PickOne.domain.user.model.domain.Email;
import com.PickOne.domain.user.model.domain.Password;
import com.PickOne.domain.user.model.domain.User;
import com.PickOne.domain.user.model.entity.UserEntity;
import com.PickOne.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ApplicationServiceTest {
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RecruitmentRepository recruitmentRepository;

    @Test
    void 멤버_지원_테스트() {
        // given
        User testUser = User.of(
                null,
                Email.of("test@example.com"),
                Password.ofEncoded("encoded-password")
        );
        User savedUser = userRepository.save(testUser);

        Recruitment recruitment = Recruitment.builder()
                .title("테스트 모집글")
                .userEntity(UserEntity.from(savedUser))
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
}
