package com.fastbite.identityservice.identity.exception;

public class DefaultRoleNotFoundException extends ApplicationException {

    public DefaultRoleNotFoundException() {
        super(ErrorCode.DEFAULT_ROLE_NOT_FOUND, "Default customer role is not configured");
    }
}
