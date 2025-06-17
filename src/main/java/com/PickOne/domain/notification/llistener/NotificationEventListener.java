package com.PickOne.domain.notification.llistener;

import com.PickOne.domain.notification.model.domain.*;
import com.PickOne.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @EventListener
    public void handleMessageSent(MessageSentEvent event) {
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
        notificationService.sendNotification(
                event.getReceiverId(),
                NotificationType.APPLICATION_RECEIVED,
                "지원서 도착",
                event.getRecruitmentTitle() + " 모집글에 지원이 들어왔습니다."
        );
    }

    @EventListener
    public void handleRecruitmentCreated(RecruitmentCreatedEvent event) {
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

        notificationService.sendNotification(
                event.getRecipientId(),
                type,
                "지원 결과 안내",
                content
        );
    }
}