package com.pickone.global.email.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "email_send_history")
public class EmailSendHistory {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String toEmail;
  private String subject;
  private String content;
  private boolean success;
  private String errorMessage;
  private LocalDateTime sentAt;

  public static EmailSendHistory create(String to, String subject, String content, boolean success, String error, LocalDateTime sentAt) {
    return new EmailSendHistory(null, to, subject, content, success, error, sentAt);
  }
}
