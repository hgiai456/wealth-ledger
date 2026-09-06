package com.giaidev.ledger.entity;

import com.giaidev.ledger.enums.AccountStatus;
import com.giaidev.ledger.enums.AccountType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class AccountTest {

    @Test
    void createShouldNormalizeDataAndStartWithZeroBalance() {
        Account account = Account.create(
                "user-1",
                "  Vietinbank  ",
                AccountType.BANK_ACCOUNT,
                "  Vietinbank  ",
                "vnd",
                false
        );

        assertEquals("user-1", account.getUserId());
        assertEquals("Vietinbank", account.getName());
        assertEquals("Vietinbank", account.getProvider());
        assertEquals("VND", account.getCurrency());
        assertEquals(0, account.getCurrentBalance().compareTo(BigDecimal.ZERO));
        assertEquals(AccountStatus.ACTIVE, account.getStatus());
        assertFalse(account.isAllowNegative());
    }

    @Test
    void closeShouldSetStatusAndClosedAt() {
        Account account = Account.create(
                "user-1",
                "Cash",
                AccountType.CASH,
                "SELF",
                "VND",
                false
        );
        Instant closedAt = Instant.parse("2026-09-04T12:00:00Z");

        account.close(closedAt);

        assertEquals(AccountStatus.CLOSED, account.getStatus());
        assertEquals(closedAt, account.getClosedAt());
    }
}
