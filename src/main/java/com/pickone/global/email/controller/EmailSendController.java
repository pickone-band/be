package com.pickone.global.email.controller;

import com.pickone.global.email.dto.EmailSendRequest;
import com.pickone.global.email.dto.EmailSendHistoryResponse;
import com.pickone.global.email.mapper.EmailSendHistoryMapper;
import com.pickone.global.email.entity.EmailSendHistory;
import com.pickone.global.email.repository.EmailSendHistoryRepository;
import com.pickone.global.email.service.EmailSendService;
import com.pickone.global.exception.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
@Tag(name = "Email", description = "이메일 전송 및 이력 관리")
public class EmailSendController {

  private final EmailSendService emailSendService;
  private final EmailSendHistoryRepository historyRepository;
  private final EmailSendHistoryMapper mapper;

  @Operation(summary = "이메일 전송", description = "입력된 정보로 이메일을 전송합니다.")
  @PostMapping("/send")
  public ResponseEntity<BaseResponse<Void>> send(@RequestBody EmailSendRequest request) {
    emailSendService.send(request);
    return BaseResponse.success();
  }

  @Operation(summary = "이메일 발송 이력 조회", description = "이메일 발송 이력을 전체 또는 특정 수신자 기준으로 조회합니다.")
  @GetMapping("/history")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<List<EmailSendHistoryResponse>>> getHistory(
      @RequestParam(required = false) String toEmail) {
    List<EmailSendHistory> histories = (toEmail == null)
        ? historyRepository.findAll()
        : historyRepository.findByToEmail(toEmail);
    return BaseResponse.success(histories.stream().map(mapper::toDto).toList());
  }
}