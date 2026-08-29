package com.giaidev.identity.exception;

import com.giaidev.core.exception.ErrorDefinition;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum IdentityErrorCode implements ErrorDefinition {

    USER_EXISTED(
            2001,
            "User existed",
            HttpStatus.CONFLICT
    ),

    USER_NOT_EXISTED(
            2002,
            "User not existed",
            HttpStatus.NOT_FOUND
    ),

    USERNAME_INVALID(
            2003,
            "Username is invalid",
            HttpStatus.BAD_REQUEST
    ),

    PASSWORD_INVALID(
            2004,
            "Password is invalid",
            HttpStatus.BAD_REQUEST
    ),

    INVALID_DOB(
            2005,
            "User must be at least {min} years old",
            HttpStatus.BAD_REQUEST
    ),

    INVALID_EMAIL(
            2006,
            "Email is invalid",
            HttpStatus.BAD_REQUEST
    ),

    USER_DISABLED(
            2007,
            "User is disabled",
            HttpStatus.FORBIDDEN
    ),

    INVALID_CREDENTIALS(
        2008,
                "Invalid username or password",
        HttpStatus.UNAUTHORIZED
        ),

    INVALID_TOKEN(
        2009,
                "Invalid or expired token",
        HttpStatus.UNAUTHORIZED
        ),
    EMAIL_EXISTED(
            2010,
            "Email already exists",
            HttpStatus.CONFLICT
    ),

    USER_DATA_CONFLICT(
            2011,
            "User data conflicts with existing data",
            HttpStatus.CONFLICT
    );



    private final int code;
    private final String message;
    private final HttpStatus statusCode;

}
