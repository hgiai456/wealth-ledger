package com.giaidev.ledger.service;

import com.giaidev.core.dto.PageResponse;
import com.giaidev.core.exception.AppException;
import com.giaidev.core.exception.CommonErrorCode;
import com.giaidev.core.security.CurrentUserProvider;
import com.giaidev.ledger.dto.response.FinancialTransactionDetailResponse;
import com.giaidev.ledger.dto.response.FinancialTransactionSummaryResponse;
import com.giaidev.ledger.dto.response.TransactionEntryResponse;
import com.giaidev.ledger.entity.FinancialTransaction;
import com.giaidev.ledger.entity.TransactionEntry;
import com.giaidev.ledger.exception.LedgerErrorCode;
import com.giaidev.ledger.mapper.FinancialTransactionMapper;
import com.giaidev.ledger.mapper.TransactionEntryMapper;
import com.giaidev.ledger.repository.AccountRepository;
import com.giaidev.ledger.repository.FinancialTransactionRepository;
import com.giaidev.ledger.repository.TransactionEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionQueryService {
    private final CurrentUserProvider currentUserProvider;

    private final AccountRepository accountRepository;

    private final FinancialTransactionRepository
            financialTransactionRepository;

    private final TransactionEntryRepository
            transactionEntryRepository;

    private final FinancialTransactionMapper
            financialTransactionMapper;

    private final TransactionEntryMapper
            transactionEntryMapper;

    public PageResponse<FinancialTransactionSummaryResponse> getHistory(
            int page,
            int size
    ){
        String userId = currentUserProvider.getUserId();

        PageRequest pageable = createPageRequest(page,size);

        Page<FinancialTransactionSummaryResponse> result =
               financialTransactionRepository
                       .findAllByUserIdOrderByTransactionDateDescIdDesc(
                               userId,
                               pageable
                       )
                       .map(financialTransactionMapper::toSummary);

       return PageResponse.from(result);
    }

    public FinancialTransactionDetailResponse getById(
            String transactionId
    ){ ///  Return Transaction Detail Response, it is included Transaction Entries List
        String userId = currentUserProvider.getUserId();

        FinancialTransaction transaction = financialTransactionRepository
                .findByIdAndUserId(transactionId, userId)
                .orElseThrow(
                        () -> new AppException(LedgerErrorCode.TRANSACTION_NOT_FOUND)
                );

        List<TransactionEntry> entries =
                transactionEntryRepository
                        .findAllByTransactionIdOrderByCreatedAtAscIdAsc(
                                transaction.getId()
                        );

        return financialTransactionMapper.toDetail(
                transaction,
                entries
        );
    }

    public PageResponse<TransactionEntryResponse> getAccountEntries(
            String accountId,
            int page,
            int size
    ){
        String userId = currentUserProvider.getUserId();

        PageRequest pageable = createPageRequest(page,size);

        accountRepository
                .findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new AppException(
                        LedgerErrorCode.ACCOUNT_NOT_FOUND
                ));

        Page<TransactionEntryResponse> result =
                transactionEntryRepository
                .findAllByAccountIdOrderByCreatedAtDescIdDesc(
                        accountId,
                        pageable
                )
                        .map(transactionEntryMapper::toResponse);

        return PageResponse.from(result);


    }

    private PageRequest createPageRequest(int page, int size){
        if(page < 0 || size < 1|| size > 100){
            throw new AppException(
                    CommonErrorCode.INVALID_REQUEST
            );
        }
        return PageRequest.of(page,size);
    }



}
