package com.pickone.global.email.service;

import com.pickone.global.email.dto.EmailSendRequest;

public interface EmailSendService {
  void send(EmailSendRequest request);
  void sendPasswordResetEmail(String toEmail, String resetToken);
  void sendAsync(EmailSendRequest request);
}
