package com.giaidev.ledger.service;

import com.giaidev.core.exception.AppException;
import com.giaidev.core.security.CurrentUserProvider;
import com.giaidev.ledger.dto.request.AccountCreationRequest;
import com.giaidev.ledger.dto.response.AccountResponse;
import com.giaidev.ledger.entity.Account;
import com.giaidev.ledger.enums.AccountStatus;
import com.giaidev.ledger.exception.LedgerErrorCode;
import com.giaidev.ledger.mapper.AccountMapper;
import com.giaidev.ledger.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
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


}
