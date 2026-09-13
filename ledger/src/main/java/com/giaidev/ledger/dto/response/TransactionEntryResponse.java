package com.giaidev.ledger.dto.response;


import com.giaidev.ledger.enums.EntryDirection;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionEntryResponse(
        String id,
        String transactionId,
        String accountId,
        String categoryId,
        EntryDirection direction,
        BigDecimal amount,
        BigDecimal balanceAfter,
        String description,
        Instant createdAt
) {
}
