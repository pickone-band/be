package com.pickone.domain.notification.controller;

import com.pickone.domain.notification.dto.NotificationDto;
import com.pickone.domain.notification.service.NotificationCommandService;
import com.pickone.domain.notification.service.NotificationQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
  private final NotificationCommandService commandService;
  private final NotificationQueryService queryService;

  @GetMapping("/{userId}")
  public List<NotificationDto> getUserNotifications(@PathVariable Long userId) {
    return queryService.getNotifications(userId);
  }

  @PostMapping
  public NotificationDto sendNotification(
      @RequestParam Long userId,
      @RequestParam String message,
      @RequestParam String type) {
    return commandService.sendNotification(userId, message, type);
  }

  @PatchMapping("/{notificationId}/read")
  public void markAsRead(@PathVariable String notificationId) {
    commandService.markAsRead(notificationId);
  }
}
