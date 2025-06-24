package com.pickone.domain.notification.listener;

import com.pickone.domain.notification.model.domain.*;
import com.pickone.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

  private final NotificationService notificationService;

  @EventListener
  public void handleMessageSent(MessageSentEvent event) {
    log.info("MessageSentEvent 수신: senderId={}, recipientIds={}, content={}",
        event.getUserId(), event.getRecipientIds(), event.getContent());

    for (Long recipientId : event.getRecipientIds()) {
      notificationService.sendNotification(
          recipientId,
          NotificationType.MESSAGE_RECEIVED,
          "새 메시지 도착",
          event.getContent()
      );
    }
  }

  @EventListener
  public void handleApplicationSubmitted(ApplicationSubmittedEvent event) {
    log.info("ApplicationSubmittedEvent 수신: receiverId={}, recruitmentTitle={}",
        event.getReceiverId(), event.getRecruitmentTitle());

    notificationService.sendNotification(
        event.getReceiverId(),
        NotificationType.APPLICATION_RECEIVED,
        "지원서 도착",
        event.getRecruitmentTitle() + " 모집글에 지원이 들어왔습니다."
    );
  }

  @EventListener
  public void handleRecruitmentCreated(RecruitmentCreatedEvent event) {
    log.info("RecruitmentCreatedEvent 수신: writerId={}, followerIds.size={}, title={}",
        event.getUserId(), event.getFollowerIds().size(), event.getTitle());

    for (Long followerId : event.getFollowerIds()) {
      notificationService.sendNotification(
          followerId,
          NotificationType.RECRUITMENT_CREATED,
          "새 모집글 등록",
          event.getTitle()
      );
    }
  }

  @EventListener
  public void handleApplicationResult(ApplicationResultEvent event) {
    NotificationType type = event.isAccepted()
        ? NotificationType.APPLICATION_ACCEPTED
        : NotificationType.APPLICATION_REJECTED;

    String content = event.getRecruitmentTitle() +
        " 모집글에 대한 지원이 " + (event.isAccepted() ? "수락" : "거절") + "되었습니다.";

    log.info("ApplicationResultEvent 수신: recipientId={}, accepted={}, title={}",
        event.getRecipientId(), event.isAccepted(), event.getRecruitmentTitle());

    notificationService.sendNotification(
        event.getRecipientId(),
        type,
        "지원 결과 안내",
        content
    );
  }

  @EventListener
  public void handleFollowedUser(FollowedUserEvent event) {
    log.info("FollowedUserEvent 수신: followerId={}, followedUserId={}",
        event.getUserId(), event.getFollowedUserId());

    notificationService.sendNotification(
        event.getFollowedUserId(),
        NotificationType.FOLLOWED_USER,
        "새 팔로워 발생",
        "새로운 사용자가 당신을 팔로우했습니다."
    );
  }
}
