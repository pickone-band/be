package com.PickOne.global.auth.service;


import com.PickOne.global.auth.model.domain.EmailMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailTemplateService {

    private final SpringTemplateEngine templateEngine;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.email.from}")
    private String fromEmail;

    public EmailMessage createVerificationEmail(String to, String token) {
        String subject = "PickOne 회원가입 이메일 인증";
        String verificationUrl = baseUrl + "/api/auth/verify?token=" + token;

        Context context = new Context();
        context.setVariable("name", to.substring(0, to.indexOf('@')));
        context.setVariable("verificationUrl", verificationUrl);
        context.setVariable("expiryHours", 24);
        context.setVariable("baseUrl", baseUrl);

        String body = templateEngine.process("email/verification", context);

        return EmailMessage.of(to, subject, body, true);
    }

    public EmailMessage createPasswordResetEmail(String to, String token) {
        String subject = "PickOne 비밀번호 재설정";
        String resetUrl = baseUrl + "/reset-password?token=" + token;

        Context context = new Context();
        context.setVariable("name", to.substring(0, to.indexOf('@')));
        context.setVariable("resetUrl", resetUrl);
        context.setVariable("expiryHours", 1);
        context.setVariable("baseUrl", baseUrl);

        String body = templateEngine.process("email/password-reset", context);

        return EmailMessage.of(to, subject, body, true);
    }

    public EmailMessage createWelcomeEmail(String to) {
        String subject = "PickOne 가입을 환영합니다!";

        Context context = new Context();
        context.setVariable("name", to.substring(0, to.indexOf('@')));
        context.setVariable("loginUrl", baseUrl + "/login");
        context.setVariable("baseUrl", baseUrl);

        String body = templateEngine.process("email/welcome", context);

        return EmailMessage.of(to, subject, body, true);
    }
}