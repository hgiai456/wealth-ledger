package com.giaidev.ledger.entity;

import com.giaidev.core.entity.CreatedEntity;
import com.giaidev.ledger.enums.CreatedByType;
import com.giaidev.ledger.enums.TransactionSourceType;
import com.giaidev.ledger.enums.TransactionStatus;
import com.giaidev.ledger.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Entity
@Table(
        name = "financial_transactions",
        schema = "ledger"
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FinancialTransaction extends CreatedEntity {
    @Column(
            name = "user_id",
            nullable = false,
            updatable = false,
            length = 36
    )
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "type",
            nullable = false,
            updatable = false,
            length = 30)
    private TransactionType type;

    @Column(
            name = "amount",
            nullable = false,
            updatable = false,
            precision = 19,
            scale = 4
    )
    private BigDecimal amount;

    @Column(
            name = "currency",
            nullable = false,
            updatable = false,
            columnDefinition = "CHAR(3)"
    )
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    private TransactionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "source_type",
            nullable = false,
            updatable = false,
            length = 30
    )
    private TransactionSourceType sourceType;

    @Column(
            name = "transaction_date",
            nullable = false,
            updatable = false
    )
    private Instant transactionDate;

    @Column(
            name = "description",
            length = 500,
            updatable = false
    )
    private String description;

    @Column(
            name = "created_by_user_id",
            length = 36,
            updatable = false
    )
    private String createdByUserId;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "created_by_type",
            nullable = false,
            updatable = false,
            length = 20
    )
    private CreatedByType createdByType;

    @Column(
            name = "idempotency_key",
            nullable = false,
            updatable = false,
            length = 100
    )
    private String idempotencyKey;

    @Column(
            name = "request_hash",
            updatable = false,
            columnDefinition = "CHAR(64)"
    )
    private String requestHash;

    @Column(
            name = "reversal_of_transaction_id",
            length = 36,
            updatable = false
    )
    private String reversalOfTransactionId;

    @Column(
            name = "reversal_reason",
            length = 500,
            updatable = false
    )
    private String reversalReason;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(
            name = "metadata",
            columnDefinition = "jsonb",
            updatable = false
    )
    private Map<String, Object> metadata;

    @Column(name = "completed_at")
    private Instant completedAt;

    private FinancialTransaction(
            String userId,
            TransactionType type,
            BigDecimal amount,
            String currency,
            TransactionSourceType sourceType,
            Instant transactionDate,
            String description,
            String createdByUserId,
            CreatedByType createdByType,
            String idempotencyKey,
            String requestHash,
            String reversalOfTransactionId,
            String reversalReason,
            Map<String, Object> metadata
    ) {
        this.userId = requireText(userId, "userId");
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.amount = requirePositiveAmount(amount);
        this.currency = normalizeCurrency(currency);
        this.status = TransactionStatus.PENDING;
        this.sourceType = Objects.requireNonNull(
                sourceType,
                "sourceType must not be null"
        );
        this.transactionDate = Objects.requireNonNull(
                transactionDate,
                "transactionDate must not be null"
        );
        this.description = optionalText(description);
        this.createdByType = Objects.requireNonNull(
                createdByType,
                "createdByType must not be null"
        );
        this.createdByUserId = validateCreator(
                createdByUserId,
                createdByType
        );
        this.idempotencyKey = requireText(
                idempotencyKey,
                "idempotencyKey"
        );
        this.requestHash = optionalText(requestHash);
        this.reversalOfTransactionId =
                optionalText(reversalOfTransactionId);
        this.reversalReason = optionalText(reversalReason);
        this.metadata = metadata == null
                ? null
                : new HashMap<>(metadata);

        validateReversalInformation();
    }

    public static FinancialTransaction create(
            String userId,
            TransactionType type,
            BigDecimal amount,
            String currency,
            TransactionSourceType sourceType,
            Instant transactionDate,
            String description,
            String createdByUserId,
            CreatedByType createdByType,
            String idempotencyKey,
            String requestHash,
            Map<String, Object> metadata
    ) {
        if (type == TransactionType.REVERSAL) {
            throw new IllegalArgumentException(
                    "Use createReversal() for reversal transaction"
            );
        }

        return new FinancialTransaction(
                userId,
                type,
                amount,
                currency,
                sourceType,
                transactionDate,
                description,
                createdByUserId,
                createdByType,
                idempotencyKey,
                requestHash,
                null,
                null,
                metadata
        );
    }

    public static FinancialTransaction createReversal(
            String userId,
            BigDecimal amount,
            String currency,
            TransactionSourceType sourceType,
            Instant transactionDate,
            String description,
            String createdByUserId,
            CreatedByType createdByType,
            String idempotencyKey,
            String requestHash,
            String originalTransactionId,
            String reversalReason,
            Map<String, Object> metadata
    ) {
        return new FinancialTransaction(
                userId,
                TransactionType.REVERSAL,
                amount,
                currency,
                sourceType,
                transactionDate,
                description,
                createdByUserId,
                createdByType,
                idempotencyKey,
                requestHash,
                requireText(
                        originalTransactionId,
                        "originalTransactionId"
                ),
                requireText(reversalReason, "reversalReason"),
                metadata
        );
    }

    public void complete(Instant completedAt) {
        requirePending();

        Instant validatedTime = Objects.requireNonNull(
                completedAt,
                "completedAt must not be null"
        );

        this.status = TransactionStatus.COMPLETED;
        this.completedAt = validatedTime;

    }

    public void fail(Instant failedAt) {
        requirePending();

         Instant validatedTime = Objects.requireNonNull(
                failedAt,
                "failedAt must not be null"
        );

        this.status = TransactionStatus.FAILED;
        this.completedAt = validatedTime;
    }

    public void markReversed() {
        if (status != TransactionStatus.COMPLETED) {
            throw new IllegalStateException(
                    "Only completed transaction can be reversed"
            );
        }
        this.status = TransactionStatus.REVERSED;
    }

    private void requirePending() {
        if (status != TransactionStatus.PENDING) {
            throw new IllegalStateException(
                    "Transaction is not pending"
            );
        }
    }

    private void validateReversalInformation() {
        boolean isReversal = type == TransactionType.REVERSAL;

        if (isReversal) {
            requireText(
                    reversalOfTransactionId,
                    "reversalOfTransactionId"
            );
            requireText(reversalReason, "reversalReason");
            return;
        }

        if (reversalOfTransactionId != null
                || reversalReason != null) {
            throw new IllegalArgumentException(
                    "Normal transaction cannot contain reversal information"
            );
        }
    }

    private static String validateCreator(
            String createdByUserId,
            CreatedByType createdByType
    ) {
        if (createdByType == CreatedByType.USER) {
            return requireText(
                    createdByUserId,
                    "createdByUserId"
            );
        }

        return optionalText(createdByUserId);
    }

    private static BigDecimal requirePositiveAmount(
            BigDecimal amount
    ) {
        Objects.requireNonNull(amount, "amount must not be null");

        if (amount.signum() <= 0) {
            throw new IllegalArgumentException(
                    "amount must be greater than zero"
            );
        }

        if (amount.stripTrailingZeros().scale() > 4) {
            throw new IllegalArgumentException(
                    "amount must have at most 4 decimal places"
            );
        }

        return amount.setScale(4);
    }

    private static String normalizeCurrency(String currency) {
        String normalized = requireText(currency, "currency")
                .toUpperCase(Locale.ROOT);

        if (!normalized.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException(
                    "currency must contain exactly 3 uppercase letters"
            );
        }

        return normalized;
    }

    private static String requireText(
            String value,
            String fieldName
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be blank"
            );
        }

        return value.trim();
    }

    private static String optionalText(String value) {
        return value == null || value.isBlank()
                ? null
                : value.trim();
    }


}
