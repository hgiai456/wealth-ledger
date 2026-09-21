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
    ),

    INSUFFICIENT_BALANCE(
            3006,
            "Account balance is insufficient",
            HttpStatus.CONFLICT
    ),

    TRANSACTION_NOT_FOUND(
        3007,
                "Transaction not found",
        HttpStatus.NOT_FOUND
    ),
    CATEGORY_NOT_FOUND(
            3008,
            "Category not found or unavailable",
            HttpStatus.NOT_FOUND
    ),

    CATEGORY_NOT_EXPENSE(
            3009,
            "Category must have type EXPENSE",
            HttpStatus.BAD_REQUEST
    ),

    OPENING_BALANCE_NOT_ALLOWED(
            3010,
            "Opening balance requires an account without ledger history",
            HttpStatus.CONFLICT
    ),

    SAME_ACCOUNT_TRANSFER(
            3011,
            "Source and destination accounts must be different",
            HttpStatus.BAD_REQUEST
    ),

    CURRENCY_MISMATCH(
            3012,
            "Accounts must use the same currency",
            HttpStatus.BAD_REQUEST
    ),

    IDEMPOTENCY_KEY_CONFLICT(
            3013,
            "Idempotency key was already used for another request",
            HttpStatus.CONFLICT
    ),

    BALANCE_LIMIT_EXCEEDED(
            3014,
            "Account balance exceeds the supported limit",
            HttpStatus.CONFLICT
    ),

    TRANSACTION_NOT_REPLAYABLE(
            3015,
            "Existing transaction cannot be replayed",
            HttpStatus.CONFLICT
    );


    private final int code;
    private final String message;
    private final HttpStatus statusCode;
}
