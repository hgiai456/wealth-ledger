package com.giaidev.ledger.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AccountUpdateRequest(
        @NotBlank(message = "Account name is required")
        @Size(max = 150, message = "Account name must not exceed 150 characters")
        String name,

        @NotBlank(message = "Provider is required")
        @Size(max = 150, message = "Provider must not exceed 150 characters")
        String provider,

        @NotNull(message = "allowNegative is required")
        Boolean allowNegative
) {
}
