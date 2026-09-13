package com.giaidev.ledger.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;

public record TransferCreationRequest(
        @NotBlank(message = "Source account ID is required")
        @Size(max = 36, message = "Source account ID is invalid")
        String sourceAccountId,

        @NotBlank(message = "Destination account ID is required")
        @Size(max = 36, message = "Destination account ID is invalid")
        String destinationAccountId,

        @NotNull(message = "Amount is required")
        @DecimalMin(
                value = "0.0001",
                message = "Amount must be greater than zero"
        )
        @Digits(
                integer = 15,
                fraction = 4,
                message = "Amount must contain at most 15 integer digits and 4 decimal places"
        )
        BigDecimal amount,

        @NotNull(message = "Transaction date is required")
        @PastOrPresent(
                message = "Transaction date cannot be in the future"
        )
        Instant transactionDate,

        @Size(
                max = 500,
                message = "Description must not exceed 500 characters"
        )
        String description
) {
}
