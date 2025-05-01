package com.PickOne.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * 애플리케이션 전역 예외를 처리하는 클래스
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * BusinessException 처리
     *
     * @param e 발생한 BusinessException 인스턴스
     * @return HTTP 상태 코드와 함께 BaseResponse 형식의 오류 응답
     */
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<BaseResponse<Void>> handleBusinessException(final BusinessException e) {
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(BaseResponse.fail(e.getErrorCode()));
    }

    /**
     * 일반적인 모든 예외 처리 (Exception)
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<BaseResponse<Void>> handleGeneralException(final Exception e) {
        log.error("Unhandled Exception 발생: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    /**
     * enum 타입이 일치하지 않을 때 발생하는 예외 처리
     *
     * @RequestParam 으로 전달된 enum 타입의 값이 맞지 않을 때 주로 발생
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<BaseResponse<String>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e) {
        log.error("MethodArgumentTypeMismatchException 예외 처리: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.fail(ErrorCode.INVALID_DATE));
    }

    // Valid 실패 (개별)
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<BaseResponse<String>> handleValidationException(HandlerMethodValidationException e) {
        // Validation 실패 메시지 추출
        String errorMessage = e.getAllErrors()
                .stream()
                .findFirst()
                .map(MessageSourceResolvable::getDefaultMessage)
                .orElse("입력한 값의 형식이 유효하지 않습니다.");

        // 에러 응답 반환
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BaseResponse.fail(errorMessage));
    }

    // @Valid 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<String>> handleValidationException(MethodArgumentNotValidException e) {
        // 첫 번째 FieldError 추출
        FieldError firstError = e.getBindingResult().getFieldErrors().get(0);
        String errorMessage = firstError.getDefaultMessage();

        System.out.println(errorMessage);
        // 첫 번째 에러 메시지 반환
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.fail(errorMessage));
    }

    /**
     * 요청에 필수적인 Path Variable이 없을 때 발생하는 예외 처리
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("HttpMessageNotReadableException 예외 처리 : {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.fail(ErrorCode.EMPTY_PATH_VARIABLE));
    }

    /**
     * 지원되지 않는 HTTP 메서드로 요청할 때 발생하는 예외 처리
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<BaseResponse<Void>> handleHttpRequestMethodNotSupportedException(
            final HttpRequestMethodNotSupportedException e) {
        log.error("HttpRequestMethodNotSupportedException 예외 처리 : {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(BaseResponse.fail(ErrorCode.METHOD_NOT_ALLOWED));
    }

    /**
     * ❌ 로그인 실패 (잘못된 ID/PW)
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<BaseResponse<Void>> handleBadCredentialsException(BadCredentialsException e) {
        log.warn("🔐 [인증 실패] 잘못된 로그인 정보: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(BaseResponse.fail(ErrorCode.AUTHORIZATION_DENIED));
    }

    /**
     * ❌ 존재하지 않는 사용자 (회원 정보 없음)
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleUsernameNotFoundException(UsernameNotFoundException e) {
        log.warn("🔐 [사용자 조회 실패] 존재하지 않는 사용자: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.fail(ErrorCode.USER_INFO_NOT_FOUND));
    }

    /**
     * ❌ 권한 부족 (접근 거부)
     */
    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<BaseResponse<Void>> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("🔐 [접근 거부] 인가되지 않은 요청: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(BaseResponse.fail(ErrorCode.HANDLE_ACCESS_DENIED));
    }

    /**
     * ❌ Spring Security 인가 예외 (권한 없음)
     */
    @ExceptionHandler(AuthorizationDeniedException.class)
    protected ResponseEntity<BaseResponse<Void>> handleAuthorizationDeniedException(
            final AuthorizationDeniedException e) {
        log.warn("🔐 [인가 실패] 권한 없음: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(BaseResponse.fail(ErrorCode.AUTHORIZATION_DENIED));
    }
}


