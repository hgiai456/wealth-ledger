package com.giaidev.ledger.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;

public record OpeningBalanceCreationRequest(
        @NotNull
        @DecimalMin("0.0001")
        @Digits(integer = 15, fraction = 4)
        BigDecimal amount,

        @NotNull
        @PastOrPresent
        Instant transactionDate,

        @Size(max = 500)
        String description
) {
}
