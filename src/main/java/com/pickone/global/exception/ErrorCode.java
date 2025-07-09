package com.pickone.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // ✅ 공통 예외
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, 1001, "잘못된 값을 입력했습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, 1002, "허용되지 않은 메서드입니다."),
    ENTITY_NOT_FOUND(HttpStatus.BAD_REQUEST, 1003, "엔티티를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 1004, "서버 오류가 발생하였습니다."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, 1005, "잘못된 유형 값을 입력하였습니다."),
    HANDLE_ACCESS_DENIED(HttpStatus.FORBIDDEN, 1006, "액세스가 거부되었습니다."),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, 1007, "비정상적 접근입니다."),
    INVALID_DATE(HttpStatus.BAD_REQUEST, 1008, "날짜 형식을 확인해주세요."),
    EMPTY_PATH_VARIABLE(HttpStatus.BAD_REQUEST, 1009, "필수 경로 변수가 누락되었습니다."),
    NOT_SUPPORTED_TYPE(HttpStatus.BAD_REQUEST, 1010, "잘못된 형식 파일입니다."),
    FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 1011, "파일 삭제 중 오류가 발생했습니다."),
    AUTHORIZATION_DENIED(HttpStatus.FORBIDDEN, 2504, "권한이 없습니다."),

    // ✅ 회원 관련
    USER_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, 2001, "해당 회원의 정보를 찾을 수 없습니다."),
    DUPLICATE_USERNAME(HttpStatus.BAD_REQUEST, 2101, "이미 사용 중인 로그인 아이디입니다."),

    ALREADY_BANNED(HttpStatus.BAD_REQUEST, 2201, "이미 정지된 회원입니다."),
    ALREADY_ACTIVE(HttpStatus.BAD_REQUEST, 2202, "이미 활성 상태의 회원입니다."),
    DUPLICATE_PHONE_NUMBER(HttpStatus.BAD_REQUEST, 2301, "이미 사용 중인 전화번호입니다."),
    PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, 2302, "해당 프로필 정보를 찾을 수 없습니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST,2303,"비밀번호 형식이 올바르지 않습니다."),


    // ✅ 회원가입 입력값 관련
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, 2401, "이메일을 입력해야 합니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, 2402, "비밀번호를 입력해야 합니다."),
    INVALID_PASSWORD_CONFIRM(HttpStatus.BAD_REQUEST, 2403, "비밀번호 재입력을 입력해야 합니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, 2404, "비밀번호와 비밀번호 확인이 일치하지 않습니다."),
    INVALID_NICKNAME(HttpStatus.BAD_REQUEST, 2405, "닉네임을 입력해야 합니다."),
    INVALID_BIRTH(HttpStatus.BAD_REQUEST, 2406, "생년월일을 입력해야 합니다."),
    INVALID_GENDER(HttpStatus.BAD_REQUEST, 2407, "성별을 입력해야 합니다."),
    REQUIRED_TERM_NOT_AGREED(HttpStatus.BAD_REQUEST, 2603, "필수 약관에 동의해야 합니다."),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, 2102, "이미 사용 중인 이메일입니다."),
    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, 2103, "이미 사용 중인 닉네임입니다."),

    // ✅ OAuth2 / 소셜 로그인
    DUPLICATE_SOCIAL_ACCOUNT(HttpStatus.BAD_REQUEST, 2401, "이미 연결된 소셜 계정입니다."),
    SOCIAL_ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, 2402, "소셜 계정을 찾을 수 없습니다."),
    SOCIAL_LOGIN_FAILED(HttpStatus.UNAUTHORIZED, 6001, "소셜 로그인에 실패하였습니다."),
    UNSUPPORTED_SOCIAL_PROVIDER(HttpStatus.BAD_REQUEST, 6002, "지원되지 않는 소셜 로그인 제공자입니다."),
    SOCIAL_USER_NOT_FOUND(HttpStatus.NOT_FOUND, 6003, "소셜 로그인 사용자를 찾을 수 없습니다."),
    INVALID_SOCIAL_TOKEN(HttpStatus.UNAUTHORIZED, 6004, "유효하지 않은 소셜 로그인 토큰입니다."),

    // ✅ 약관
    DUPLICATE_TERM_VERSION(HttpStatus.BAD_REQUEST, 2501, "이미 존재하는 약관 버전입니다."),
    TERM_NOT_FOUND(HttpStatus.NOT_FOUND, 2502, "해당 약관 정보를 찾을 수 없습니다."),
    CONSENT_NOT_FOUND(HttpStatus.NOT_FOUND, 2605, "해당 동의 내역을 찾을 수 없습니다."),
    CONSENT_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, 2606, "이미 등록된 약관 동의 내역입니다."),
    INVALID_CONSENT_OPERATION(HttpStatus.BAD_REQUEST, 2607, "잘못된 동의 처리 요청입니다."),
    TERM_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, 2504, "이미 등록된 약관입니다."),

    // ✅ 인증 및 로그인
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, 4002, "인증되지 않은 사용자입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, 4003, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, 4004, "토큰이 만료되었습니다."),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, 4005, "토큰이 요청에서 누락되었습니다."),
    ALREADY_LOGGED_OUT(HttpStatus.UNAUTHORIZED, 4006, "이미 로그아웃된 사용자입니다."),
    JWT_SIGNATURE_MISMATCH(HttpStatus.UNAUTHORIZED, 4007, "JWT 서명이 유효하지 않습니다."),
    JWT_MALFORMED(HttpStatus.UNAUTHORIZED, 4008, "잘못된 형식의 JWT 토큰입니다."),
    INVALID_PASSWORD_CREDENTIAL(HttpStatus.UNAUTHORIZED, 4104, "비밀번호가 올바르지 않습니다."),
    ALREADY_VERIFIED(HttpStatus.BAD_REQUEST, 4101, "이미 인증된 사용자입니다."),
    EMAIL_NOT_VERIFIED(HttpStatus.UNAUTHORIZED, 4105, "이메일 인증이 완료되지 않았습니다."),
    SAME_AS_OLD_PASSWORD(HttpStatus.BAD_REQUEST, 4106, "새 비밀번호가 기존 비밀번호와 동일합니다."),
    LOGIN_USER_NOT_FOUND(HttpStatus.BAD_REQUEST, 4107, "해당 이메일로 등록된 사용자가 없습니다."),

    // ✅ Refresh Token
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, 7001, "유효하지 않은 리프레시 토큰입니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, 7002, "리프레시 토큰이 만료되었습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, 7003, "해당 리프레시 토큰을 찾을 수 없습니다."),
    TOKEN_STORAGE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 7004, "토큰 저장 중 오류가 발생했습니다."),
    TOKEN_DELETION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 7005, "토큰 삭제 중 오류가 발생했습니다."),

    // ✅ JWT 블랙리스트
    BLACKLISTED_TOKEN(HttpStatus.UNAUTHORIZED, 8001, "해당 토큰은 블랙리스트에 등록되어 사용할 수 없습니다."),
    TOKEN_ALREADY_BLACKLISTED(HttpStatus.BAD_REQUEST, 8002, "해당 토큰은 이미 블랙리스트에 등록되어 있습니다."),

    // ✅ 모집 관련
    INVALID_RECRUITMENT_ID(HttpStatus.UNAUTHORIZED, 9001, "존재하지 않는 모집글입니다."),
    UNAUTHORIZED_RECRUITMENT_ACCESS(HttpStatus.FORBIDDEN, 9002, "해당 모집글에 대한 권한이 없습니다."),
    DUPLICATE_APPLICATION(HttpStatus.FORBIDDEN, 9101, "이미 지원한 모집글입니다."),
    APPLICATION_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, 9201, "해당 모집글에 대한 신청글을 찾을 수 없습니다."),

    // ✅ 채팅/메시지 관련
    ALREADY_READ(HttpStatus.BAD_REQUEST, 8001, "이미 읽은 메시지입니다."),
    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, 8002, "채팅방을 찾을 수 없습니다."),
    CHAT_ROOM_ACCESS_DENIED(HttpStatus.FORBIDDEN, 8003, "채팅방에 참여하지 않았습니다."),
    CHAT_ROOM_DELETE_FORBIDDEN(HttpStatus.FORBIDDEN, 8004, "채팅방을 삭제할 권한이 없습니다."),

    CANNOT_FOLLOW_SELF(HttpStatus.BAD_REQUEST, 3101, "자신을 팔로우할 수 없습니다."),
    ALREADY_FOLLOWING(HttpStatus.BAD_REQUEST, 3102, "이미 팔로우한 사용자입니다."),
    FOLLOW_RELATION_NOT_FOUND(HttpStatus.NOT_FOUND, 3103, "팔로우 관계를 찾을 수 없습니다."),

    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, 9401, "알림을 찾을 수 없습니다."),;

    private final HttpStatus status;
    private final int code;
    private final String message;
}
