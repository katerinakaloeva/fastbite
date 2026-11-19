package com.fastbite.identityservice.identity.exception;

import lombok.Getter;

@Getter
public abstract class ApplicationException extends RuntimeException {

    private final ErrorCode code;

    protected ApplicationException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }
}
