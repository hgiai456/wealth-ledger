package com.giaidev.ledger.exception;

import com.giaidev.core.exception.ErrorDefinition;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum LedgerErrorCode implements ErrorDefinition {

    ACCOUNT_NOT_FOUND(
            3001,
            "Account not found",
            HttpStatus.NOT_FOUND
    ),

    ACCOUNT_NAME_EXISTED(
            3002,
            "An account with this name already exists",
            HttpStatus.CONFLICT
    ),

    ACCOUNT_BALANCE_NOT_ZERO(
            3003,
            "Account balance must be zero before closing",
            HttpStatus.CONFLICT
    ),

    ACCOUNT_CLOSED(
            3004,
            "Closed account cannot be updated",
            HttpStatus.CONFLICT
    ),

    NEGATIVE_BALANCE_NOT_ALLOWED(
            3005,
            "Cannot disable negative balance while the account balance is negative",
            HttpStatus.CONFLICT
    );

    private final int code;
    private final String message;
    private final HttpStatus statusCode;
}
