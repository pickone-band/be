package com.pickone.global.email.service;

import com.pickone.global.email.dto.EmailSendRequestDto;

public interface EmailSendService {
  void send(EmailSendRequestDto request);
}
