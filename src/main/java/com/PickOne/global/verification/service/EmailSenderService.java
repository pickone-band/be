package com.PickOne.global.verification.service;

import com.PickOne.global.verification.dto.EmailMessage;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import static io.lettuce.core.pubsub.PubSubOutput.Type.message;

@Service
@Slf4j
public class EmailSenderService {

    private final JavaMailSender mailSender;
    private final String fromEmail;
    private final boolean emailEnabled;

    public EmailSenderService(JavaMailSender mailSender,
                              @Value("${mail.from}") String fromEmail,
                              @Value("${mail.enabled:true}") boolean emailEnabled) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
        this.emailEnabled = emailEnabled;
    }

    public void sendEmail(EmailMessage emailMessage) {
        if (!emailEnabled) {
            log.info("이메일 발송이 비활성화되어 있습니다. 다음 이메일이 발송되었을 것입니다: {}", emailMessage.to());
            return;
        }

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

            helper.setTo(emailMessage.to());
            helper.setSubject(emailMessage.subject());
            helper.setText(emailMessage.body(), true);
            helper.setFrom(fromEmail);

            mailSender.send(mimeMessage);

            log.info("{}로 이메일이 성공적으로 발송되었습니다.", emailMessage.to());

        } catch (Exception e) {
            log.error("{}로 이메일 발송에 실패했습니다: {}", emailMessage.to(), e.getMessage(), e);
            throw new EmailSendException("이메일 발송 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    public static class EmailSendException extends RuntimeException {
        public EmailSendException(String message) {
            super(message);
        }

        public EmailSendException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
