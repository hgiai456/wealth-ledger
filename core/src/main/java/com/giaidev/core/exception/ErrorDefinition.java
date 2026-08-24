package com.giaidev.core.exception;

import org.springframework.http.HttpStatus;

public interface ErrorDefinition {
    int getCode();
    String getMessage();
    HttpStatus getStatusCode();
}
