package com.fastbite.identityservice.identity.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT),
    DEFAULT_ROLE_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR);

    private final HttpStatus status;

    ErrorCode(HttpStatus status) {
        this.status = status;
    }
}
