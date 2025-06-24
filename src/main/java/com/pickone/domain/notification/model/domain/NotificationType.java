package com.pickone.domain.notification.model.domain;

import lombok.Getter;

@Getter
public enum NotificationType {

  // 메시지
  MESSAGE_RECEIVED("새 메시지가 도착했습니다"),

  // 모집 관련
  RECRUITMENT_CREATED("새 모집글이 등록되었습니다"),
  APPLICATION_RECEIVED("새 모집 신청이 도착했습니다"),
  APPLICATION_ACCEPTED("지원이 수락되었습니다"),
  APPLICATION_REJECTED("지원이 거절되었습니다"),

  // 사용자 활동
  FOLLOWED_USER("팔로우 알림"),

  // 시스템
  SYSTEM_ANNOUNCEMENT("시스템 공지사항");

  private final String defaultMessage;

  NotificationType(String defaultMessage) {
    this.defaultMessage = defaultMessage;
  }

}