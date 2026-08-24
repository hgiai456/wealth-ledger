package com.giaidev.core.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {

    private final ErrorDefinition errorCode;

    public AppException(ErrorDefinition errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
