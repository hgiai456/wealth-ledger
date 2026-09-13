package com.giaidev.ledger.entity;

import com.giaidev.core.entity.BaseEntity;
import com.giaidev.core.exception.AppException;
import com.giaidev.ledger.enums.AccountStatus;
import com.giaidev.ledger.enums.AccountType;
import com.giaidev.ledger.exception.LedgerErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;

@Entity
@Table(
        name = "accounts",
        schema = "ledger"
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends BaseEntity {
    @Column(
            name = "user_id",
            nullable = false,
            length = 36,
            updatable = false
    )
    private String userId;

    @Column(
            name = "name",
            nullable = false,
            length = 150
    )
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "type",
            nullable = false,
            length = 30
    )
    private AccountType type;

    @Column(
            name = "provider",
            nullable = false,
            length = 150
    )
    private String provider;

    @Column(
            name = "currency",
            nullable = false,
            columnDefinition = "CHAR(3)"
    )
    private String currency;

    @Column(
            name = "current_balance",
            nullable = false,
            precision = 19,
            scale = 4
    )
    private BigDecimal currentBalance;

    @Column(
            name = "allow_negative",
            nullable = false
    )
    private boolean allowNegative;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    private AccountStatus status;

    @Version
    @Column(
            name = "version",
            nullable = false
    )
    private long version;

    @Column(name = "closed_at")
    private Instant closedAt;

    private Account(
            String userId,
            String name,
            AccountType type,
            String provider,
            String currency,
            boolean allowNegative
    ) {
        this.userId = Objects.requireNonNull(userId);
        this.name = Objects.requireNonNull(name).trim();
        this.type = Objects.requireNonNull(type);
        this.provider = Objects.requireNonNull(provider).trim();
        this.currency = Objects.requireNonNull(currency)
                .trim()
                .toUpperCase(Locale.ROOT);

        // Balance ban đầu luôn bằng 0.
        // Opening balance phải được tạo bằng ledger transaction.
        this.currentBalance = new BigDecimal("0.0000");
        this.allowNegative = allowNegative;
        this.status = AccountStatus.ACTIVE;
    }

    public static Account create(
            String userId,
            String name,
            AccountType type,
            String provider,
            String currency,
            boolean allowNegative
    ) {
        return new Account(
                userId,
                name,
                type,
                provider,
                currency,
                allowNegative
        );
    }

    public void updateDetails(
            String name,
            String provider,
            boolean allowNegative
    ) {
        this.name = Objects.requireNonNull(name).trim();
        this.provider = Objects.requireNonNull(provider).trim();
        this.allowNegative = allowNegative;
    }

    public void close(Instant closedAt) {
        if (status == AccountStatus.CLOSED) {
            return;
        }
        this.status = AccountStatus.CLOSED;
        this.closedAt = Objects.requireNonNull(closedAt);
    }

    private void ensureActive() {
        if (status != AccountStatus.ACTIVE) {
            throw new AppException(
                    LedgerErrorCode.ACCOUNT_CLOSED
            );
        }
    }

    public BigDecimal debit(BigDecimal amount){
        ensureActive();

        BigDecimal normalizedAmount =
                requirePositiveAmount(amount);

        BigDecimal balanceAfter =
                currentBalance.subtract(normalizedAmount);

        if (!allowNegative && balanceAfter.signum() < 0) {
            throw new AppException(
                    LedgerErrorCode.INSUFFICIENT_BALANCE
            );
        }

        this.currentBalance = balanceAfter;

        return this.currentBalance;
    }


    private static BigDecimal requirePositiveAmount(
            BigDecimal amount
    ) {
        Objects.requireNonNull(
                amount,
                "amount must not be null"
        );

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


    public BigDecimal credit(BigDecimal amount) {
        ensureActive();

        BigDecimal normalizedAmount =
                requirePositiveAmount(amount);

        this.currentBalance =
                currentBalance.add(normalizedAmount);

        return this.currentBalance;
    }
}
