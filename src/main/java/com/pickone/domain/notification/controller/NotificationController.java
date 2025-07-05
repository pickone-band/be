  package com.pickone.domain.notification.controller;

  import com.pickone.domain.notification.dto.CreateNotificationRequest;
  import com.pickone.domain.notification.dto.NotificationDto;
  import com.pickone.domain.notification.service.NotificationCommandService;
  import com.pickone.domain.notification.service.NotificationQueryService;

  import com.pickone.global.exception.BaseResponse;
  import lombok.RequiredArgsConstructor;
  import org.springframework.http.ResponseEntity;
  import org.springframework.web.bind.annotation.*;

  import java.util.List;

  @RestController
  @RequestMapping("/api/notifications")
  @RequiredArgsConstructor
  public class NotificationController {
    private final NotificationCommandService commandService;
    private final NotificationQueryService queryService;

    @GetMapping("/{userId}")
    public ResponseEntity<BaseResponse<List<NotificationDto>>> getUserNotifications(@PathVariable Long userId) {
      List<NotificationDto> notifications = queryService.getNotifications(userId);
      return BaseResponse.success(notifications);
    }

    @PostMapping
    public ResponseEntity<BaseResponse<NotificationDto>> sendNotification(
        @RequestBody CreateNotificationRequest request
    ) {
      NotificationDto notification = commandService.sendNotification(request.userId(),
          request.message(),
          request.type());
      return BaseResponse.success(notification);
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<BaseResponse<Void>> markAsRead(@PathVariable String notificationId) {
      commandService.markAsRead(notificationId);
      return BaseResponse.success();
    }
  }

