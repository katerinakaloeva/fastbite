package com.fastbite.identityservice.identity.exception;

public class EmailAlreadyExistsException extends ApplicationException {

    public EmailAlreadyExistsException() {
        super(ErrorCode.EMAIL_ALREADY_EXISTS, "Email is already registered");
    }
}
