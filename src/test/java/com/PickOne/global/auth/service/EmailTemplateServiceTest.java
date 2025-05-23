package com.PickOne.global.auth.service;

import com.PickOne.global.auth.model.domain.EmailMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(properties = {
        "mail.from=noreply@pickone.com",
        "mail.enabled=true"
})
class EmailTemplateServiceTest {

    @Autowired
    private EmailTemplateService emailTemplateService;

    @Test
    void 이메일_인증_템플릿_생성() {
        String email = "test@pickone.com";
        String token = "sample-token";

        EmailMessage message = emailTemplateService.createVerificationEmail(email, token);

        assertThat(message.getTo()).isEqualTo(email);
        assertThat(message.getSubject()).contains("회원가입");
        assertThat(message.getBody()).contains("sample-token");
        assertThat(message.isHtml()).isTrue();
    }

    @Test
    void 비밀번호_재설정_템플릿_생성() {
        String email = "test@pickone.com";
        String token = "reset-token";

        EmailMessage message = emailTemplateService.createPasswordResetEmail(email, token);

        assertThat(message.getTo()).isEqualTo(email);
        assertThat(message.getSubject()).contains("비밀번호");
        assertThat(message.getBody()).contains("reset-token");
        assertThat(message.isHtml()).isTrue();
    }

    @Test
    void 환영_이메일_템플릿_생성() {
        String email = "test@pickone.com";

        EmailMessage message = emailTemplateService.createWelcomeEmail(email);

        assertThat(message.getTo()).isEqualTo(email);
        assertThat(message.getSubject()).contains("환영");
        assertThat(message.isHtml()).isTrue();
    }
}
