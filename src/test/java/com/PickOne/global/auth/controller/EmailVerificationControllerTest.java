package com.PickOne.global.auth.controller;

import com.PickOne.domain.user.model.domain.Email;
import com.PickOne.domain.user.model.domain.Password;
import com.PickOne.domain.user.model.domain.User;
import com.PickOne.domain.user.service.UserService;
import com.PickOne.global.auth.dto.PasswordResetRequest;
import com.PickOne.global.auth.dto.VerificationResponse;
import com.PickOne.global.auth.service.EmailVerificationService;
import com.PickOne.global.exception.BaseResponse;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EmailVerificationControllerTest {

    private final EmailVerificationService emailVerificationService = mock(EmailVerificationService.class);
    private final UserService userService = mock(UserService.class);
    private final EmailVerificationController controller = new EmailVerificationController(emailVerificationService, userService);

    private final User mockUser = User.of(
            1L,
            Email.of("test@pickone.com"),
            Password.ofEncoded("encodedPassword"),
            false
    );

    @Test
    @DisplayName("이메일 인증 재전송 성공")
    void resendVerificationEmail_success() {
        when(userService.findByEmail("test@pickone.com")).thenReturn(mockUser);

        ResponseEntity<BaseResponse<Void>> response = controller.resendVerificationEmail("test@pickone.com");

        assertEquals(200, response.getStatusCodeValue());
        verify(emailVerificationService).sendVerificationEmail(mockUser);
    }

    @Test
    @DisplayName("이미 인증된 사용자 이메일 재전송 실패")
    void resendVerificationEmail_alreadyVerified() {
        User verifiedUser = User.of(1L, Email.of("verified@pickone.com"), Password.ofEncoded("pass"), true);
        when(userService.findByEmail("verified@pickone.com")).thenReturn(verifiedUser);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                controller.resendVerificationEmail("verified@pickone.com"));
        assertEquals(ErrorCode.ALREADY_VERIFIED, exception.getErrorCode());
    }

    @Test
    @DisplayName("이메일 인증 토큰 검증 성공")
    void verifyEmail_success() {
        when(emailVerificationService.verifyEmail("test-token")).thenReturn(true);

        ResponseEntity<BaseResponse<VerificationResponse>> response = controller.verifyEmail("test-token");

        assertTrue(response.getBody().getResult().success());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("비밀번호 재설정 요청 성공")
    void forgotPassword_success() {
        when(userService.findByEmail("test@pickone.com")).thenReturn(mockUser);

        ResponseEntity<BaseResponse<Void>> response = controller.forgotPassword("test@pickone.com");

        verify(emailVerificationService).sendPasswordResetEmail(mockUser);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("비밀번호 재설정 토큰 유효 확인")
    void validateResetToken_success() {
        when(emailVerificationService.validatePasswordResetToken("reset-token")).thenReturn(mockUser);

        ResponseEntity<BaseResponse<VerificationResponse>> response = controller.validateResetToken("reset-token");

        assertTrue(response.getBody().getResult().success());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("비밀번호 재설정 성공")
    void resetPassword_success() {
        PasswordResetRequest request = new PasswordResetRequest("newPass123", "newPass123");

        when(emailVerificationService.validatePasswordResetToken("token")).thenReturn(mockUser);

        ResponseEntity<BaseResponse<Void>> response = controller.resetPassword("token", request);

        verify(userService).updatePassword(mockUser.getId(), request.newPassword());
        verify(emailVerificationService).completePasswordReset("token");

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("비밀번호 불일치로 재설정 실패")
    void resetPassword_passwordMismatch() {
        PasswordResetRequest request = new PasswordResetRequest("pass1", "pass2");

        BusinessException exception = assertThrows(BusinessException.class, () ->
                controller.resetPassword("token", request));

        assertEquals(ErrorCode.PASSWORD_MISMATCH, exception.getErrorCode());
    }
}
