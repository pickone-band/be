package com.pickone.domain.notification.listener;

import com.pickone.domain.notification.event.*;
import com.pickone.domain.notification.model.domain.NotificationType;
import com.pickone.domain.notification.service.NotificationCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

  private final NotificationCommandService commandService;

  @EventListener
  public void handleFollowedUserEvent(FollowedUserEvent event) {
    log.info("[NotificationEventListener] 팔로우 알림 이벤트 수신 - targetUserId: {}, message: {}",
        event.getTargetUserId(), event.getMessage());
    send(event.getTargetUserId(), event.getMessage(), NotificationType.FOLLOW);
  }

  @EventListener
  public void handleMessageSentEvent(MessageSentEvent event) {
    log.info("[NotificationEventListener] 메시지 전송 이벤트 수신 - senderId: {}, 대상 수: {}",
        event.getSenderId(), event.getRecipientIds().size());
    for (Long targetUserId : event.getRecipientIds()) {
      send(targetUserId, event.getMessage(), NotificationType.MESSAGE);
    }
  }

  @EventListener
  public void handleApplicationSubmittedEvent(ApplicationSubmittedEvent event) {
    log.info("[NotificationEventListener] 지원서 제출 이벤트 수신 - targetUserId: {}, message: {}",
        event.getTargetUserId(), event.getMessage());
    send(event.getTargetUserId(), event.getMessage(), NotificationType.APPLICATION);
  }

  @EventListener
  public void handleApplicationResultEvent(ApplicationResultEvent event) {
    log.info("[NotificationEventListener] 지원 결과 이벤트 수신 - targetUserId: {}, 승인 여부: {}, message: {}",
        event.getTargetUserId(), event.isApproved(), event.getMessage());
    send(event.getTargetUserId(), event.getMessage(), NotificationType.APPLICATION);
  }

  @EventListener
  public void handleRecruitmentCreatedEvent(RecruitmentCreatedEvent event) {
    log.info("[NotificationEventListener] 모집글 생성 이벤트 수신 - targetUserId: {}, recruitmentId: {}",
        event.getTargetUserId(), event.getRecruitmentId());
    send(event.getTargetUserId(), event.getMessage(), NotificationType.RECRUITMENT);
  }

  private void send(Long userId, String message, NotificationType type) {
    commandService.sendNotification(userId, message, type);
    log.info("[NotificationEventListener] 알림 전송 완료 - userId: {}, type: {}, message: {}", userId, type, message);
  }
}
