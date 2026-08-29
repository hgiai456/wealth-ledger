package com.giaidev.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;


@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ApiErrorResponse(int code,
                               String message,
                               List<FieldViolation> errors) {

    public ApiErrorResponse(int code, String message) {
        this(code, message, List.of());
    }
}

