package com.giaidev.ledger.dto.response;

import com.giaidev.ledger.enums.CreatedByType;
import com.giaidev.ledger.enums.TransactionSourceType;
import com.giaidev.ledger.enums.TransactionStatus;
import com.giaidev.ledger.enums.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public record FinancialTransactionDetailResponse(
        String id,
        TransactionType type,
        BigDecimal amount,
        String currency,
        TransactionStatus status,
        TransactionSourceType sourceType,
        Instant transactionDate,
        String description,
        CreatedByType createdByType,
        String reversalOfTransactionId,
        String reversalReason,
        Map<String, Object> metadata,
        Instant createdAt,
        Instant completedAt,
        List<TransactionEntryResponse> entries
) {
    public FinancialTransactionDetailResponse {
        entries = entries == null
                ? List.of()
                : List.copyOf(entries);
    }
}
