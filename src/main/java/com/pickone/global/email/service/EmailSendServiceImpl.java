package com.pickone.global.email.service;

import com.pickone.global.email.dto.EmailSendRequest;
import com.pickone.global.email.entity.EmailSendHistory;
import com.pickone.global.email.repository.EmailSendHistoryRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSendServiceImpl implements EmailSendService {

  private final JavaMailSender mailSender;
  private final EmailSendHistoryRepository historyRepository;

  @Override
  @Transactional
  public void send(EmailSendRequest request) {
    boolean success = false;
    String errorMsg = null;

    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setTo(request.to());
      helper.setSubject(request.subject());
      helper.setText(request.content(), true); // true → HTML 허용

      mailSender.send(message);
      success = true;
      log.info("HTML 이메일 발송 성공: {}", request.to());

    } catch (MessagingException e) {
      errorMsg = e.getMessage();
      log.error("HTML 이메일 발송 실패: {}", errorMsg, e);

    } finally {
      historyRepository.save(EmailSendHistory.create(
          request.to(),
          request.subject(),
          request.content(),
          success,
          errorMsg,
          LocalDateTime.now()
      ));
    }

    if (!success) {
      throw new RuntimeException("이메일 발송 실패: " + errorMsg);
    }
  }

  @Override
  @Transactional
  public void sendPasswordResetEmail(String email, String token) {
    log.info("[EmailService] 비밀번호 재설정 이메일 발송 시작: email={}, token={}", email, token);

    String resetUrl = "https://your-frontend-url.com/reset-password?token=" + token;

    String subject = "비밀번호 재설정 안내";
    String content = """
      <html>
        <body>
          <p>안녕하세요.</p>
          <p>비밀번호 재설정을 요청하셨습니다. 아래 링크를 클릭하여 비밀번호를 재설정해 주세요:</p>
          <p><a href="%s">비밀번호 재설정하기</a></p>
          <p>본 링크는 1시간 동안 유효합니다.</p>
          <p>감사합니다.</p>
        </body>
      </html>
      """.formatted(resetUrl);

    EmailSendRequest request = new EmailSendRequest(email, subject, content);
    this.send(request);  // HTML 전송 지원됨

    log.info("[EmailService] 비밀번호 재설정 이메일 발송 완료: email={}", email);
  }

  @Async
  @Override
  @Transactional
  public void sendAsync(EmailSendRequest request) {
    this.send(request);
  }
}
