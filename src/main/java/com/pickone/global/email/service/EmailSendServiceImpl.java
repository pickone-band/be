package com.pickone.global.email.service;

import com.pickone.global.email.dto.EmailSendRequestDto;
import com.pickone.global.email.model.entity.EmailSendHistoryEntity;
import com.pickone.global.email.repository.EmailSendHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSendServiceImpl implements EmailSendService {
  private final JavaMailSender mailSender;
  private final EmailSendHistoryRepository historyRepository;

  @Override
  @Transactional
  public void send(EmailSendRequestDto request) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(request.to());
    message.setSubject(request.subject());
    message.setText(request.content());

    boolean success = false;
    String errorMsg = null;
    try {
      mailSender.send(message);
      success = true;
      log.info("이메일 발송 성공: {}", request.to());
    } catch (Exception e) {
      errorMsg = e.getMessage();
      log.error("이메일 발송 실패: {}", errorMsg, e);
    } finally {
      historyRepository.save(
          EmailSendHistoryEntity.of(
              request.to(),
              request.subject(),
              request.content(),
              success,
              errorMsg,
              LocalDateTime.now()
          )
      );
    }
    if (!success) {
      throw new RuntimeException("이메일 발송에 실패했습니다: " + errorMsg);
    }
  }
}
