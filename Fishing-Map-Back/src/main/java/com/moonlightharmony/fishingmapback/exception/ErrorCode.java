package com.moonlightharmony.fishingmapback.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),
    FILE_STORE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 저장에 실패했습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    FISH_SPECIES_NOT_FOUND(HttpStatus.NOT_FOUND, "어종을 찾을 수 없습니다.")
    ;
    
    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message){
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() { return status; }
    public String getMessage() { return message; }
}
