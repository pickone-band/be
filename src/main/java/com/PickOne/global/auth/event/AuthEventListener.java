package com.PickOne.global.auth.event;

import com.PickOne.domain.user.model.domain.User;
import com.PickOne.domain.user.service.UserService;
import com.PickOne.global.auth.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthEventListener {

    private final EmailVerificationService emailVerificationService;
    private final UserService userService;

    /**
     * 회원가입 완료 시 이메일 인증 메일 발송
     */
    @Async
    @TransactionalEventListener
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        log.info("사용자 {}의 회원가입 이벤트를 처리하고 있습니다", event.getUserId());

        try {
            User user = userService.findById(event.getUserId());
            emailVerificationService.sendVerificationEmail(user);
        } catch (Exception e) {
            log.error("사용자 {}의 인증 이메일 발송에 실패했습니다: {}",
                    event.getUserId(), e.getMessage(), e);
        }
    }

    /**
     * 이메일 인증 완료 시 다른 서비스에 알림
     */
    @Async
    @EventListener
    public void handleEmailVerifiedEvent(EmailVerifiedEvent event) {
        log.info("사용자 {}가 이메일 {}을 인증했습니다", event.getUserId(), event.getEmail());

        // 다른 서비스에 알림 로직 추가 가능
        // 예: 사용자 온보딩 서비스, 환영 이메일 발송 등
    }
}