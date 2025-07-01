package com.pickone.global.email.service;

import com.pickone.global.email.dto.EmailSendRequestDto;
import com.pickone.global.email.model.entity.EmailSendHistoryEntity;
import com.pickone.global.email.repository.EmailSendHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EmailSendServiceImplTest {

  @Mock private JavaMailSender mailSender;
  @Mock private EmailSendHistoryRepository historyRepository;
  @InjectMocks private EmailSendServiceImpl sut;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Nested
  @DisplayName("send")
  class Send {
    @Test
    @DisplayName("이메일 정상 발송, 히스토리 기록 및 예외 없음")
    void send_success() {
      EmailSendRequestDto req = new EmailSendRequestDto("to@sample.com", "subject", "content");

      // mailSender.send()가 정상 동작
      doNothing().when(mailSender).send(any(SimpleMailMessage.class));

      EmailSendHistoryEntity saved = mock(EmailSendHistoryEntity.class);
      // EmailSendHistoryEntity.of()를 static으로 처리하려면 static mocking 필요, 보통 실제 객체 사용
      when(historyRepository.save(any(EmailSendHistoryEntity.class))).thenReturn(saved);

      // when-then
      sut.send(req);

      verify(mailSender).send(any(SimpleMailMessage.class));
      verify(historyRepository).save(any(EmailSendHistoryEntity.class));
    }

    @Test
    @DisplayName("이메일 발송 실패 시 히스토리 기록 + 예외 발생")
    void send_fail() {
      EmailSendRequestDto req = new EmailSendRequestDto("fail@sample.com", "subject", "content");

      // mailSender.send()에서 예외 발생
      doThrow(new RuntimeException("smtp error")).when(mailSender).send(any(SimpleMailMessage.class));

      EmailSendHistoryEntity saved = mock(EmailSendHistoryEntity.class);
      when(historyRepository.save(any(EmailSendHistoryEntity.class))).thenReturn(saved);

      assertThatThrownBy(() -> sut.send(req))
          .isInstanceOf(RuntimeException.class)
          .hasMessageContaining("이메일 발송에 실패했습니다")
          .hasMessageContaining("smtp error");

      verify(mailSender).send(any(SimpleMailMessage.class));
      verify(historyRepository).save(any(EmailSendHistoryEntity.class));
    }
  }
}
