package com.pickone.domain.notification.service;

import com.pickone.domain.notification.dto.NotificationDto;
import com.pickone.domain.notification.model.entity.NotificationDocument;
import com.pickone.domain.notification.model.mapper.NotificationMapper;
import com.pickone.domain.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationQueryServiceImplTest {

  @Mock private NotificationRepository notificationRepository;
  @InjectMocks private NotificationQueryServiceImpl sut;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("알림 목록을 최신순으로 반환")
  void getNotifications_success() {
    Long userId = 10L;
    NotificationDocument doc1 = mock(NotificationDocument.class);
    NotificationDocument doc2 = mock(NotificationDocument.class);
    List<NotificationDocument> docs = Arrays.asList(doc1, doc2);

    when(notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(docs);

    NotificationDto dto1 = mock(NotificationDto.class);
    NotificationDto dto2 = mock(NotificationDto.class);

    try (MockedStatic<NotificationMapper> staticMapper = mockStatic(NotificationMapper.class)) {
      staticMapper.when(() -> NotificationMapper.toDto(doc1)).thenReturn(dto1);
      staticMapper.when(() -> NotificationMapper.toDto(doc2)).thenReturn(dto2);

      List<NotificationDto> result = sut.getNotifications(userId);

      assertThat(result).containsExactly(dto1, dto2);
      verify(notificationRepository).findByUserIdOrderByCreatedAtDesc(userId);
    }
  }

  @Test
  @DisplayName("알림이 없으면 빈 리스트 반환")
  void getNotifications_empty() {
    Long userId = 20L;
    when(notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(Collections.emptyList());

    List<NotificationDto> result = sut.getNotifications(userId);

    assertThat(result).isEmpty();
    verify(notificationRepository).findByUserIdOrderByCreatedAtDesc(userId);
  }
}
