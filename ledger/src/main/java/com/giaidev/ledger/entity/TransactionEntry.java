package com.giaidev.ledger.entity;

import com.giaidev.core.entity.CreatedEntity;
import com.giaidev.ledger.enums.EntryDirection;
import com.giaidev.ledger.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(
        name = "transaction_entries",
        schema = "ledger"
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransactionEntry extends CreatedEntity {
    @Column(
            name = "transaction_id",
            nullable = false,
            updatable = false,
            length = 36
    )
    private String transactionId;

    @Column(
            name = "account_id",
            nullable = false,
            updatable = false,
            length = 36
    )
    private String accountId;

    @Column(
            name = "category_id",
            updatable = false,
            length = 36
    )
    private String categoryId;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "direction",
            nullable = false,
            updatable = false,
            length = 10
    )
    private EntryDirection direction;

    @Column(
            name = "amount",
            nullable = false,
            updatable = false,
            precision = 19,
            scale = 4
    )
    private BigDecimal amount;

    @Column(
            name = "balance_after",
            nullable = false,
            updatable = false,
            precision = 19,
            scale = 4
    )
    private BigDecimal balanceAfter;

    @Column(
            name = "description",
            updatable = false,
            length = 500
    )
    private String description;

    private TransactionEntry(
            String transactionId,
            String accountId,
            String categoryId,
            EntryDirection direction,
            BigDecimal amount,
            BigDecimal balanceAfter,
            String description
    ) {
        this.transactionId = requireText(
                transactionId,
                "transactionId"
        );
        this.accountId = requireText(accountId, "accountId");
        this.categoryId = optionalText(categoryId);
        this.direction = Objects.requireNonNull(
                direction,
                "direction must not be null"
        );
        this.amount = requirePositiveAmount(amount);
        this.balanceAfter = normalizeMoney(
                balanceAfter,
                "balanceAfter"
        );
        this.description = optionalText(description);
    }

    public static TransactionEntry create(
            String transactionId,
            String accountId,
            String categoryId,
            EntryDirection direction,
            BigDecimal amount,
            BigDecimal balanceAfter,
            String description
    ) {
        return new TransactionEntry(
                transactionId,
                accountId,
                categoryId,
                direction,
                amount,
                balanceAfter,
                description
        );
    }

    private static BigDecimal requirePositiveAmount(
            BigDecimal amount
    ) {
        BigDecimal normalized = normalizeMoney(
                amount,
                "amount"
        );

        if (normalized.signum() <= 0) {
            throw new IllegalArgumentException(
                    "amount must be greater than zero"
            );
        }

        return normalized;
    }

    private static BigDecimal normalizeMoney(
            BigDecimal value,
            String fieldName
    ) {
        Objects.requireNonNull(
                value,
                fieldName + " must not be null"
        );

        if (value.stripTrailingZeros().scale() > 4) {
            throw new IllegalArgumentException(
                    fieldName
                            + " must have at most 4 decimal places"
            );
        }

        return value.setScale(4);
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
