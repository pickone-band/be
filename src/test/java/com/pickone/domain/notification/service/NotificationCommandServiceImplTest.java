package com.pickone.domain.notification.service;

import com.pickone.domain.notification.dto.NotificationDto;
import com.pickone.domain.notification.model.domain.NotificationType;
import com.pickone.domain.notification.model.entity.NotificationDocument;
import com.pickone.domain.notification.model.mapper.NotificationMapper;
import com.pickone.domain.notification.repository.NotificationRepository;
import com.pickone.global.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationCommandServiceImplTest {

  @Mock
  private NotificationRepository notificationRepository;
  @InjectMocks
  private NotificationCommandServiceImpl sut;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Nested
  @DisplayName("sendNotification")
  class SendNotification {

    @Test
    @DisplayName("정상적으로 알림 전송")
    void sendNotification_success() {
      Long userId = 1L;
      String message = "좋아요가 도착했습니다!";
      NotificationType notificationType = NotificationType.LIKE;

      NotificationDocument mockDoc = mock(NotificationDocument.class);
      NotificationDocument savedDoc = mock(NotificationDocument.class);
      NotificationDto mockDto = mock(NotificationDto.class);

      try (MockedStatic<NotificationDocument> staticDoc = mockStatic(NotificationDocument.class)) {
        staticDoc.when(() -> NotificationDocument.of(eq(userId), eq(message), eq(notificationType)))
            .thenReturn(mockDoc);

        when(notificationRepository.save(mockDoc)).thenReturn(savedDoc);

        try (MockedStatic<NotificationMapper> staticMapper = mockStatic(NotificationMapper.class)) {
          staticMapper.when(() -> NotificationMapper.toDto(savedDoc))
              .thenReturn(mockDto);

          NotificationDto result = sut.sendNotification(userId, message, notificationType);

          assertThat(result).isSameAs(mockDto);
          verify(notificationRepository).save(mockDoc);
        }
      }
    }

    @Test
    @DisplayName("알림 타입 대소문자 무관 변환")
    void sendNotification_caseInsensitiveType() {
      Long userId = 2L;
      String message = "팔로우 알림입니다";
      NotificationType notificationType = NotificationType.FOLLOW;

      NotificationDocument mockDoc = mock(NotificationDocument.class);
      NotificationDocument savedDoc = mock(NotificationDocument.class);
      NotificationDto mockDto = mock(NotificationDto.class);

      try (MockedStatic<NotificationDocument> staticDoc = mockStatic(NotificationDocument.class)) {
        staticDoc.when(() -> NotificationDocument.of(eq(userId), eq(message), eq(notificationType)))
            .thenReturn(mockDoc);

        when(notificationRepository.save(mockDoc)).thenReturn(savedDoc);

        try (MockedStatic<NotificationMapper> staticMapper = mockStatic(NotificationMapper.class)) {
          staticMapper.when(() -> NotificationMapper.toDto(savedDoc))
              .thenReturn(mockDto);

          NotificationDto result = sut.sendNotification(userId, message, notificationType);

          assertThat(result).isSameAs(mockDto);
          verify(notificationRepository).save(mockDoc);
        }
      }
    }

    @Nested
    @DisplayName("markAsRead")
    class MarkAsRead {

      @Test
      @DisplayName("정상적으로 읽음 처리")
      void markAsRead_success() {
        String notiId = "abc123";

        NotificationDocument doc = mock(NotificationDocument.class);
        NotificationDocument savedDoc = mock(NotificationDocument.class);

        when(notificationRepository.findById(notiId)).thenReturn(Optional.of(doc));
        doNothing().when(doc).markRead();
        when(notificationRepository.save(doc)).thenReturn(savedDoc);

        sut.markAsRead(notiId);

        verify(notificationRepository).findById(notiId);
        verify(doc).markRead();
        verify(notificationRepository).save(doc);
      }

      @Test
      @DisplayName("알림이 없으면 예외 발생")
      void markAsRead_notFound() {
        String notiId = "not_found";
        when(notificationRepository.findById(notiId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut.markAsRead(notiId))
            .isInstanceOf(BusinessException.class)
        ;
      }
    }
  }
}
