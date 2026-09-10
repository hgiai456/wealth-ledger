package com.giaidev.ledger.exception;

import com.giaidev.core.dto.ApiErrorResponse;
import com.giaidev.core.dto.ApiResponse;
import com.giaidev.core.dto.FieldViolation;
import com.giaidev.core.exception.AppException;
import com.giaidev.core.exception.CommonErrorCode;
import com.giaidev.core.exception.ErrorDefinition;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;


import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE) //
@RestControllerAdvice(basePackages = "com.giaidev.ledger.controller") //Just apply for this controller of module
public class LedgerValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Void>> handleRequestBodyValidation(
            MethodArgumentNotValidException exception
    ) {
        Map<String, String> errors = exception
            .getBindingResult()
            .getFieldErrors()
            .stream()
            .collect(Collectors.toMap(
                    FieldError::getField,
                    this::resolveMessage,
                    // Nếu một field có nhiều lỗi thì lấy lỗi đầu tiên
                    (first, second) -> first,
                    LinkedHashMap::new
            ));

        CommonErrorCode errorCode =
                CommonErrorCode.INVALID_REQUEST;


        ApiResponse<Void> response =
                ApiResponse.validationError(
                        errorCode.getCode(),
                        errorCode.getMessage(),
                        errors
                );

        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(response);
    }
    //"Invalid value"

    private String resolveMessage(FieldError fieldError){
        String messageKey = Objects.requireNonNullElse(
                fieldError.getDefaultMessage(),
                "Invalid value"
                );

        try {
            LedgerErrorCode errorCode = LedgerErrorCode.valueOf(messageKey);

            return  interpolateMessage(
                    errorCode.getMessage(),
                    fieldError
            );
        } catch (IllegalArgumentException e) {

            return messageKey;
        }
    }

    private String interpolateMessage(
            String message,
            FieldError fieldError
            ){
        try {
            ConstraintViolation<?> violation = fieldError.unwrap(ConstraintViolation.class);

            String resolvedMessage = message;

            for (Map.Entry<String, Object> attribute :
                violation.getConstraintDescriptor()
                        .getAttributes()
                        .entrySet()
            ){
                resolvedMessage = resolvedMessage.replace(
                        "{" + attribute.getKey() + "}",
                        String.valueOf(attribute.getValue())
                );
            }

            return resolvedMessage;
        }catch (IllegalArgumentException exception){
            return message;
        }
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    ResponseEntity<ApiErrorResponse> handleMethodValidation(
            HandlerMethodValidationException exception
    ) {
        List<FieldViolation> errors = exception
                .getAllValidationResults()
                .stream()
                .flatMap(result -> result
                        .getResolvableErrors()
                        .stream()
                        .map(error -> new FieldViolation(
                                Objects.requireNonNullElse(
                                        result.getMethodParameter()
                                                .getParameterName(),
                                        "parameter"
                                ),
                                Objects.requireNonNullElse(
                                        error.getDefaultMessage(),
                                        "Invalid value"
                                )
                        )))
                .toList();

        return invalidRequest(errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ApiErrorResponse> handleConstraintViolation(
            ConstraintViolationException exception
    ) {
        List<FieldViolation> errors = exception
                .getConstraintViolations()
                .stream()
                .map(violation -> new FieldViolation(
                        violation.getPropertyPath().toString(),
                        violation.getMessage()
                ))
                .toList();

        return invalidRequest(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ApiErrorResponse> handleMalformedJson(
            HttpMessageNotReadableException exception
    ) {
        CommonErrorCode error =
                CommonErrorCode.INVALID_REQUEST;

        return ResponseEntity
                .status(error.getStatusCode())
                .body(new ApiErrorResponse(
                        error.getCode(),
                        "Request body is malformed"
                ));
    }

    @ExceptionHandler(AppException.class)
    ResponseEntity<ApiResponse<Void>> handleAppException(
            AppException exception
    ) {
        ErrorDefinition error = exception.getErrorCode();

        ApiResponse<Void> response = ApiResponse.error(
               error.getCode(),
                error.getMessage()
        );

        return ResponseEntity
                .status(error.getStatusCode())
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiResponse<Void>> handleUnexpectedException(
            Exception exception
    ) {
        CommonErrorCode error =
                CommonErrorCode.UNCATEGORIZED_EXCEPTION;

        log.error("Unhandled application exception", exception);

        return ResponseEntity
                .status(error.getStatusCode())
                .body(ApiResponse.error(
                        error.getCode(),
                        error.getMessage()
                ));
    }

    private ResponseEntity<ApiErrorResponse> invalidRequest(
            List<FieldViolation> errors
    ) {
        CommonErrorCode error =
                CommonErrorCode.INVALID_REQUEST;

        return ResponseEntity
                .status(error.getStatusCode())
                .body(new ApiErrorResponse(
                        error.getCode(),
                        error.getMessage(),
                        errors
                ));
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ApiResponse<Void>> handleAccessDenied(
            AccessDeniedException exception
    ){
        CommonErrorCode errorCode = CommonErrorCode.UNAUTHORIZE;

        log.error("Unhandled application exception", exception);

        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(ApiResponse.error(
                        errorCode.getCode(),
                        errorCode.getMessage()
                ));
    }

}