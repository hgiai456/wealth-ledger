package com.giaidev.ledger.dto.response;

import com.giaidev.ledger.enums.TransactionSourceType;
import com.giaidev.ledger.enums.TransactionStatus;
import com.giaidev.ledger.enums.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;

public record FinancialTransactionSummaryResponse(
        String id,
        TransactionType type,
        BigDecimal amount,
        String currency,
        TransactionStatus status,
        TransactionSourceType sourceType,
        Instant transactionDate,
        String description,
        String reversalOfTransactionId,
        Instant createdAt,
        Instant completedAt
) {
}
