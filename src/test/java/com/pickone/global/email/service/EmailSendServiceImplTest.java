package com.pickone.global.email.service;

import com.pickone.global.email.dto.EmailSendRequest;
import com.pickone.global.email.entity.EmailSendHistory;
import com.pickone.global.email.repository.EmailSendHistoryRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmailSendServiceImplTest {

  @InjectMocks
  private EmailSendServiceImpl emailSendService;

  @Mock
  private JavaMailSender mailSender;

  @Mock
  private EmailSendHistoryRepository historyRepository;

  @Mock
  private MimeMessage mimeMessage;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
  }

  @Test
  void send_success() throws Exception {
    EmailSendRequest request = new EmailSendRequest("test@example.com", "Test Subject", "<p>Content</p>");

    doNothing().when(mailSender).send(mimeMessage);

    emailSendService.send(request);

    verify(mailSender).send(mimeMessage);
    verify(historyRepository).save(any(EmailSendHistory.class));
  }

  @Test
  void send_failure_shouldThrowException() throws Exception {
    EmailSendRequest request = new EmailSendRequest("fail@example.com", "Fail Subject", "<p>Fail</p>");

    doAnswer(invocation -> {
      throw new RuntimeException("Fail to send");
    }).when(mailSender).send(mimeMessage);

    assertThrows(RuntimeException.class, () -> emailSendService.send(request));

    verify(historyRepository).save(any(EmailSendHistory.class));
  }

  @Test
  void sendPasswordResetEmail_success() {
    String email = "reset@example.com";
    String token = "abc123";

    EmailSendServiceImpl spyService = spy(emailSendService);
    doNothing().when(spyService).send(any(EmailSendRequest.class));

    spyService.sendPasswordResetEmail(email, token);

    verify(spyService).send(any(EmailSendRequest.class));
  }
}