package com.giaidev.ledger.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReversalCreationRequest(
        @NotBlank(message = "Reversal reason is required")
        @Size(
                max = 500,
                message = "Reversal reason must not exceed 500 characters"
        )
        String reason
)
{
}
