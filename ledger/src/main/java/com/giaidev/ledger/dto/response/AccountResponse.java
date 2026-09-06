package com.giaidev.ledger.dto.response;

import com.giaidev.ledger.enums.AccountStatus;
import com.giaidev.ledger.enums.AccountType;

import java.math.BigDecimal;
import java.time.Instant;

public record AccountResponse(
        String id,
        String name,
        AccountType type,
        String provider,
        String currency,
        BigDecimal currentBalance,
        boolean allowNegative,
        AccountStatus status,
        long version,
        Instant createdAt,
        Instant updatedAt,
        Instant closedAt
) {
}
