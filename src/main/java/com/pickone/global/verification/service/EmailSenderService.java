package com.pickone.global.verification.service;

import com.pickone.global.verification.dto.EmailMessage;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

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
      log.info("이메일 비활성화 설정됨 - 발송 생략됨: {}", emailMessage.to());
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
      log.info("이메일 발송 성공: to={}", emailMessage.to());

    } catch (Exception e) {
      log.error("이메일 발송 실패: to={}, message={}", emailMessage.to(), e.getMessage(), e);
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
