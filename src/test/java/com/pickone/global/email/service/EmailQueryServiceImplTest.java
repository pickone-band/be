package com.pickone.global.email.service;

import com.pickone.global.email.dto.EmailSendHistoryResponse;
import com.pickone.global.email.entity.EmailSendHistory;
import com.pickone.global.email.mapper.EmailSendHistoryMapper;
import com.pickone.global.email.repository.EmailSendHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class EmailQueryServiceImplTest {

  @InjectMocks
  private EmailQueryServiceImpl emailQueryService;

  @Mock
  private EmailSendHistoryRepository historyRepository;

  @Mock
  private EmailSendHistoryMapper emailMapper;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void getAllHistory_success() {
    EmailSendHistory history = EmailSendHistory.create("test@example.com", "제목", "내용", true, null, null);
    EmailSendHistoryResponse response = new EmailSendHistoryResponse(1L, "test@example.com", "제목", true, null);

    when(historyRepository.findAll()).thenReturn(List.of(history));
    when(emailMapper.toDto(history)).thenReturn(response);

    List<EmailSendHistoryResponse> result = emailQueryService.getAllHistory();

    assertEquals(1, result.size());
    assertEquals("test@example.com", result.get(0).toEmail());
  }
}