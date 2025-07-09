package com.pickone.global.email.service;

import com.pickone.global.email.dto.EmailSendHistoryResponse;
import com.pickone.global.email.mapper.EmailSendHistoryMapper;
import com.pickone.global.email.repository.EmailSendHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailQueryServiceImpl implements EmailQueryService {

  private final EmailSendHistoryRepository historyRepository;
  private final EmailSendHistoryMapper emailMapper;

  @Override
  public List<EmailSendHistoryResponse> getAllHistory() {
    var results = historyRepository.findAll().stream()
        .map(emailMapper::toDto)
        .toList();

    log.info("[EmailQueryService] 전체 이메일 발송 기록 조회 - 총 {}건", results.size());
    return results;
  }
}
