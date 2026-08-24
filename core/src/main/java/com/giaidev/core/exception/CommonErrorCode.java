package com.giaidev.core.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorDefinition {

    UNCATEGORIZED_EXCEPTION(
            9999,
            "Uncategorized error",
            HttpStatus.INTERNAL_SERVER_ERROR
    ),

    INVALID_REQUEST(
            1001,
            "Invalid request",
            HttpStatus.BAD_REQUEST
    ),

    UNAUTHENTICATED(1002, "Unauthenticated", HttpStatus.UNAUTHORIZED),

    UNAUTHORIZE(1003, "You do not have permission", HttpStatus.FORBIDDEN);

    private final int code;
    private final String message;
    private final HttpStatus statusCode;
}
