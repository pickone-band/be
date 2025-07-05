package com.pickone.domain.notification.listener;

import com.pickone.domain.notification.event.ApplicationResultEvent;
import com.pickone.domain.notification.event.ApplicationSubmittedEvent;
import com.pickone.domain.notification.event.FollowedUserEvent;
import com.pickone.domain.notification.event.MessageSentEvent;
import com.pickone.domain.notification.event.RecruitmentCreatedEvent;
import com.pickone.domain.notification.model.domain.*;
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
    commandService.sendNotification(event.getTargetUserId(), event.getMessage(), NotificationType.FOLLOW);
  }
  @EventListener
  public void handleMessageSentEvent(MessageSentEvent event) {
    for (Long targetUserId : event.getRecipientIds()) {
      commandService.sendNotification(targetUserId, event.getMessage(), NotificationType.MESSAGE);
    }
  }

  @EventListener
  public void handleApplicationSubmittedEvent(ApplicationSubmittedEvent event) {
    commandService.sendNotification(event.getTargetUserId(), event.getMessage(), NotificationType.APPLICATION);
  }

  @EventListener
  public void handleApplicationResultEvent(ApplicationResultEvent event) {
    commandService.sendNotification(event.getTargetUserId(), event.getMessage(), NotificationType.APPLICATION);
  }

  @EventListener
  public void handleRecruitmentCreatedEvent(RecruitmentCreatedEvent event) {
    commandService.sendNotification(event.getTargetUserId(), event.getMessage(), NotificationType.RECRUITMENT);
  }
}
