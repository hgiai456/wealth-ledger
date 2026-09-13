package com.giaidev.ledger.entity;

import com.giaidev.core.exception.AppException;
import com.giaidev.ledger.enums.AccountStatus;
import com.giaidev.ledger.enums.AccountType;
import com.giaidev.ledger.exception.LedgerErrorCode;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void debitShouldDecreaseBalance() {
        Account account = Account.create(
                "user-01",
                "Tiền mặt",
                AccountType.CASH,
                "Cá nhân",
                "VND",
                false
        );

        account.credit(new BigDecimal("100000"));

        BigDecimal balanceAfter =
                account.debit(new BigDecimal("50000"));

        assertEquals(
                new BigDecimal("50000.0000"),
                balanceAfter
        );
    }

    @Test
    void debitShouldRejectInsufficientBalance() {
        Account account = Account.create(
                "user-01",
                "Tiền mặt",
                AccountType.CASH,
                "Cá nhân",
                "VND",
                false
        );

        AppException exception = assertThrows(
                AppException.class,
                () -> account.debit(
                        new BigDecimal("50000")
                )
        );

        assertEquals(
                LedgerErrorCode.INSUFFICIENT_BALANCE,
                exception.getErrorCode()
        );
    }

    @Test
    void creditShouldIncreaseBalance() {
        Account account = Account.create(
                "user-01",
                "Momo",
                AccountType.E_WALLET,
                "Momo",
                "VND",
                false
        );

        BigDecimal balanceAfter =
                account.credit(new BigDecimal("500000"));

        assertEquals(
                new BigDecimal("500000.0000"),
                balanceAfter
        );
    }
}
