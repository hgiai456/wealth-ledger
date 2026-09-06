package com.giaidev.ledger.dto.request;

import com.giaidev.ledger.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AccountCreationRequest(
        @NotBlank(message = "Account name is required")
        @Size(max = 150, message = "Account name must not exceed 150 characters")
        String name,

        @NotNull(message = "Account type is required")
        AccountType type,

        @NotBlank(message = "Provider is required")
        @Size(max = 150, message = "Provider must not exceed 150 characters")
        String provider,

        @NotBlank(message = "Currency is required")
        @Pattern(
                regexp = "^[A-Za-z]{3}$",
                message = "Currency must be a 3-letter ISO code"
        )
        String currency,

        boolean allowNegative
) {
}
