package com.giaidev.ledger.service;

import com.giaidev.core.exception.AppException;
import com.giaidev.core.security.CurrentUserProvider;
import com.giaidev.ledger.dto.request.AccountCreationRequest;
import com.giaidev.ledger.entity.Account;
import com.giaidev.ledger.enums.AccountType;
import com.giaidev.ledger.exception.LedgerErrorCode;
import com.giaidev.ledger.mapper.AccountMapper;
import com.giaidev.ledger.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        when(currentUserProvider.getUserId()).thenReturn("user-1");
    }

    @Test
    void createShouldAlwaysUseAuthenticatedUserId() {
        AccountCreationRequest request = new AccountCreationRequest(
                "Momo",
                AccountType.E_WALLET,
                "Momo",
                "VND",
                false
        );
        when(accountRepository.existsByUserIdAndNameIgnoreCase(
                "user-1",
                "Momo"
        )).thenReturn(false);
        when(accountRepository.saveAndFlush(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        accountService.create(request);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).saveAndFlush(captor.capture());
        assertEquals("user-1", captor.getValue().getUserId());
        assertEquals("Momo", captor.getValue().getName());
    }

    @Test
    void createShouldRejectDuplicateNameForSameUser() {
        AccountCreationRequest request = new AccountCreationRequest(
                "Momo",
                AccountType.E_WALLET,
                "Momo",
                "VND",
                false
        );
        when(accountRepository.existsByUserIdAndNameIgnoreCase(
                "user-1",
                "Momo"
        )).thenReturn(true);

        AppException exception = assertThrows(
                AppException.class,
                () -> accountService.create(request)
        );

        assertEquals(
                LedgerErrorCode.ACCOUNT_NAME_EXISTED,
                exception.getErrorCode()
        );
        verify(accountRepository, never()).saveAndFlush(any(Account.class));
    }
}
