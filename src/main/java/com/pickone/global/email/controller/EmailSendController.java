package com.pickone.global.email.controller;

import com.pickone.global.email.dto.EmailSendHistoryDto;
import com.pickone.global.email.dto.EmailSendRequestDto;
import com.pickone.global.email.model.entity.EmailSendHistoryEntity;
import com.pickone.global.email.model.mapper.EmailSendHistoryMapper;
import com.pickone.global.email.repository.EmailSendHistoryRepository;
import com.pickone.global.email.service.EmailSendService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailSendController {
  private final EmailSendService emailSendService;
  private final EmailSendHistoryRepository historyRepository;

  @PostMapping("/send")
  public void send(@RequestBody EmailSendRequestDto request) {
    emailSendService.send(request);
  }

  @GetMapping("/history")
  @PreAuthorize("hasRole('ADMIN')") // 반드시 관리자만 접근
  public List<EmailSendHistoryDto> getHistory(@RequestParam(required = false) String toEmail) {
    List<EmailSendHistoryEntity> histories = (toEmail == null)
        ? historyRepository.findAll()
        : historyRepository.findByToEmail(toEmail);
    return histories.stream()
        .map(EmailSendHistoryMapper::toDto)
        .toList();
  }

}
