package com.giaidev.ledger.service;

import com.giaidev.core.exception.AppException;
import com.giaidev.core.security.CurrentUserProvider;
import com.giaidev.ledger.dto.request.AccountCreationRequest;
import com.giaidev.ledger.dto.request.AccountUpdateRequest;
import com.giaidev.ledger.dto.response.AccountResponse;
import com.giaidev.ledger.entity.Account;
import com.giaidev.ledger.enums.AccountStatus;
import com.giaidev.ledger.enums.AccountType;
import com.giaidev.ledger.exception.LedgerErrorCode;
import com.giaidev.ledger.mapper.AccountMapper;
import com.giaidev.ledger.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public AccountResponse create(AccountCreationRequest request){
        String userId = currentUserProvider.getUserId();
        String normalizeName = request.name().trim();

        if(accountRepository.existsByUserIdAndNameIgnoreCase(
                userId,
                normalizeName
        )){
            throw new AppException(LedgerErrorCode.ACCOUNT_NAME_EXISTED);
        }

        Account account = Account.create(
                userId,
                normalizeName,
                request.type(),
                request.provider(),
                request.currency(),
                request.allowNegative()
        );

        try {
            Account savedAccount = accountRepository.saveAndFlush(account);
            return accountMapper.toResponse(savedAccount);
        } catch (DataIntegrityViolationException exception) {
           throw new AppException(LedgerErrorCode.ACCOUNT_NAME_EXISTED) ;
        }
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getAll(AccountStatus status){
        String userId = currentUserProvider.getUserId();

        List<Account> accounts;

        if(status == null){
            accounts = accountRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
        }else {
            accounts = accountRepository.findAllByUserIdAndStatusOrderByCreatedAtDesc(
                    userId,
                    status
            );
        }
        return accountMapper.toResponse(accounts);
    }

    private Account getOwnedAccount(String accountId) {
        String userId = currentUserProvider.getUserId();
        return getOwnedAccount(accountId, userId);
    }

    private Account getOwnedAccount(
            String accountId,
            String userId
    ) {
        return accountRepository
                .findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new AppException(
                        LedgerErrorCode.ACCOUNT_NOT_FOUND
                ));
    }



    @Transactional(readOnly = true)
    public AccountResponse getById(String accountId){
        Account account = getOwnedAccount(accountId);

        return accountMapper.toResponse(account);
    }

    @Transactional
    public AccountResponse update(
            String accountId,
            AccountUpdateRequest request
    ){
        String userId = currentUserProvider.getUserId();

        Account account = getOwnedAccount(accountId, userId);

        if(account.getStatus() == AccountStatus.CLOSED){
            throw new AppException(
                    LedgerErrorCode.ACCOUNT_CLOSED
            );
        }

        String normalizedName = request.name().trim();

        boolean duplicatedName = accountRepository
                .existsByUserIdAndNameIgnoreCaseAndIdNot(
                        userId,
                        normalizedName,
                        accountId
                );
        if(duplicatedName){
            throw new AppException(
                    LedgerErrorCode.ACCOUNT_NAME_EXISTED
            );
        }

        boolean allowNegative = request.allowNegative();

        if(!allowNegative
                && account.getCurrentBalance()
                    .compareTo(BigDecimal.ZERO) < 0){
            //If current balance < 0 and allowNegative == false => throw error
            throw new AppException(
                    LedgerErrorCode.NEGATIVE_BALANCE_NOT_ALLOWED
            );
        }

        account.updateDetails(
                normalizedName,
                request.provider(),
                allowNegative
        );

        try {
            Account savedAccount = accountRepository.saveAndFlush(account);
            return accountMapper.toResponse(savedAccount);
        }catch (DataIntegrityViolationException exception){
            throw new AppException(
                    LedgerErrorCode.ACCOUNT_NAME_EXISTED
            );
        }

    }

    @Transactional
    public AccountResponse close(String accountId){
        Account account = getOwnedAccount(accountId);

        if(account.getStatus() == AccountStatus.CLOSED){
            return accountMapper.toResponse(account);
        }

        if (account.getCurrentBalance()
                .compareTo(BigDecimal.ZERO) != 0) {
            throw new AppException(
                    LedgerErrorCode.ACCOUNT_BALANCE_NOT_ZERO
            );
        }

        account.close(Instant.now());

        Account savedAccount = accountRepository.save(account);

        return accountMapper.toResponse(savedAccount);
    }


}
