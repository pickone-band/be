package com.PickOne.global.auth.service;

import com.PickOne.global.auth.model.domain.EmailMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailSenderServiceTest {

    private JavaMailSender mailSender;
    private EmailSenderService emailSenderService;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        emailSenderService = new EmailSenderService(mailSender, "noreply@pickone.com", true); // 명시적 주입
    }

    @Test
    void 이메일_정상_발송() {
        EmailMessage message = EmailMessage.of("user@pickone.com", "제목", "본문", false);
        emailSenderService.sendEmail(message);
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void 이메일_비활성화시_발송되지_않음() {
        emailSenderService = new EmailSenderService(mailSender, "noreply@pickone.com", false);
        EmailMessage message = EmailMessage.of("user@pickone.com", "제목", "본문", false);
        emailSenderService.sendEmail(message);
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }
}