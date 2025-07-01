package com.pickone.domain.notification.service;

import com.pickone.domain.notification.dto.NotificationDto;
import com.pickone.domain.notification.model.domain.NotificationType;
import com.pickone.domain.notification.model.entity.NotificationDocument;
import com.pickone.domain.notification.model.mapper.NotificationMapper;
import com.pickone.domain.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationCommandServiceImplTest {

  @Mock private NotificationRepository notificationRepository;
  @InjectMocks private NotificationCommandServiceImpl sut;

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
      String message = "msg";
      String type = "LIKE";
      NotificationType notificationType = NotificationType.LIKE;

      NotificationDocument doc = mock(NotificationDocument.class);
      NotificationDocument saved = mock(NotificationDocument.class);

      // static factory mock
      try (MockedStatic<NotificationDocument> docStatic = mockStatic(NotificationDocument.class)) {
        docStatic.when(() -> NotificationDocument.of(
            eq(userId), eq(message), eq(notificationType))
        ).thenReturn(doc);

        when(notificationRepository.save(doc)).thenReturn(saved);

        NotificationDto dto = mock(NotificationDto.class);
        try (MockedStatic<NotificationMapper> mapperStatic = mockStatic(NotificationMapper.class)) {
          mapperStatic.when(() -> NotificationMapper.toDto(saved)).thenReturn(dto);

          NotificationDto result = sut.sendNotification(userId, message, type);

          assertThat(result).isSameAs(dto);

          verify(notificationRepository).save(doc);
        }
      }
    }

    @Test
    @DisplayName("알림 타입 대소문자 무관 변환")
    void sendNotification_caseInsensitiveType() {
      Long userId = 2L;
      String message = "alarm";
      String type = "like"; // 소문자

      NotificationType notificationType = NotificationType.LIKE;
      NotificationDocument doc = mock(NotificationDocument.class);
      NotificationDocument saved = mock(NotificationDocument.class);

      try (MockedStatic<NotificationDocument> docStatic = mockStatic(NotificationDocument.class)) {
        docStatic.when(() -> NotificationDocument.of(
            eq(userId), eq(message), eq(notificationType))
        ).thenReturn(doc);

        when(notificationRepository.save(doc)).thenReturn(saved);

        NotificationDto dto = mock(NotificationDto.class);
        try (MockedStatic<NotificationMapper> mapperStatic = mockStatic(NotificationMapper.class)) {
          mapperStatic.when(() -> NotificationMapper.toDto(saved)).thenReturn(dto);

          NotificationDto result = sut.sendNotification(userId, message, type);

          assertThat(result).isSameAs(dto);
          verify(notificationRepository).save(doc);
        }
      }
    }

    @Test
    @DisplayName("알 수 없는 타입은 valueOf에서 예외 발생")
    void sendNotification_invalidType() {
      assertThatThrownBy(() -> sut.sendNotification(1L, "msg", "unknownType"))
          .isInstanceOf(IllegalArgumentException.class);
    }
  }

  @Nested
  @DisplayName("markAsRead")
  class MarkAsRead {
    @Test
    @DisplayName("정상적으로 읽음 처리")
    void markAsRead_success() {
      String notiId = "123";
      NotificationDocument doc = mock(NotificationDocument.class);
      NotificationDocument saved = mock(NotificationDocument.class);

      when(notificationRepository.findById(notiId)).thenReturn(Optional.of(doc));
      doNothing().when(doc).markRead();
      when(notificationRepository.save(doc)).thenReturn(saved);

      sut.markAsRead(notiId);

      verify(notificationRepository).findById(notiId);
      verify(doc).markRead();
      verify(notificationRepository).save(doc);
    }

    @Test
    @DisplayName("알림이 없으면 예외 발생")
    void markAsRead_notFound() {
      String notiId = "x";
      when(notificationRepository.findById(notiId)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> sut.markAsRead(notiId))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Not found");
    }
  }
}
