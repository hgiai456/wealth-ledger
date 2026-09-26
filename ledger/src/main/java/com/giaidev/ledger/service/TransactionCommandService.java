package com.giaidev.ledger.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.giaidev.core.exception.AppException;
import com.giaidev.core.exception.CommonErrorCode;
import com.giaidev.core.security.CurrentUserProvider;
import com.giaidev.ledger.dto.request.CashFlowCreationRequest;
import com.giaidev.ledger.dto.request.OpeningBalanceCreationRequest;
import com.giaidev.ledger.dto.request.TransferCreationRequest;
import com.giaidev.ledger.dto.response.FinancialTransactionDetailResponse;
import com.giaidev.ledger.entity.Account;
import com.giaidev.ledger.entity.Category;
import com.giaidev.ledger.entity.FinancialTransaction;
import com.giaidev.ledger.entity.TransactionEntry;
import com.giaidev.ledger.enums.*;
import com.giaidev.ledger.exception.LedgerErrorCode;
import com.giaidev.ledger.mapper.FinancialTransactionMapper;
import com.giaidev.ledger.repository.AccountRepository;
import com.giaidev.ledger.repository.CategoryRepository;
import com.giaidev.ledger.repository.FinancialTransactionRepository;
import com.giaidev.ledger.repository.TransactionEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_COMMITTED)
public class TransactionCommandService {
    private final CurrentUserProvider currentUserProvider;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final FinancialTransactionRepository transactionRepository;
    private final TransactionEntryRepository transactionEntryRepository;
    private final FinancialTransactionMapper transactionMapper;
    private final ObjectMapper objectMapper;

    private Account lockOwnedAccount(
            String accountId,
            String userId
    ){
        return accountRepository.findOwnedForUpdate(accountId, userId)
                .orElseThrow(() -> new AppException(
                        LedgerErrorCode.ACCOUNT_NOT_FOUND
                ));
    }

    private String requireKey(String key){
        if(key == null || key.isBlank() || key.length() > 100){
            throw new AppException(CommonErrorCode.INVALID_REQUEST);
        }

        return key;
    }

    private String normalizeDescription(String value){
        return value == null ? "" : value.trim();
    }

//    Cùng key + cùng nghiệp vụ/nội dung → trả giao dịch cũ
//    Cùng key + khác nghiệp vụ/nội dung → báo xung đột
    private String requestHash(String... fields){
        try {
            byte[] payload = objectMapper.writeValueAsBytes(fields);
            byte[] digest = MessageDigest
                    .getInstance("SHA-256")
                    .digest(payload);

            return HexFormat.of().formatHex(digest);

        } catch (JsonProcessingException | NoSuchAlgorithmException ex) {
            throw new IllegalStateException(
                    "Cannot calculate request hash",
                    ex
            );
        }
    }

    private Optional<FinancialTransactionDetailResponse> replay(
            String userId,
            String key,
            String hash
    ){
        return transactionRepository
                .findByUserIdAndIdempotencyKey(userId, key)
                .map(transaction -> {
                    if(!Objects.equals(
                            transaction.getRequestHash(),
                            hash
                    )){
                        throw new AppException(
                                LedgerErrorCode.IDEMPOTENCY_KEY_CONFLICT
                        );
                    }

                    if (transaction.getStatus() != TransactionStatus.COMPLETED
                            && transaction.getStatus() != TransactionStatus.REVERSED) {
                        throw new AppException(
                                LedgerErrorCode.TRANSACTION_NOT_REPLAYABLE
                        );
                    }

                    List<TransactionEntry> entries = transactionEntryRepository
                            .findAllByTransactionIdOrderByCreatedAtAscIdAsc(
                                    transaction.getId()
                            );

                    return transactionMapper.toDetail(
                            transaction,
                            entries
                    );
                }); /// Return Transaction Detail and List of Transaction entries
    }

    /// Create Financial Transaction with pending status
    private FinancialTransaction createPending(
            String userId,
            TransactionType type,
            BigDecimal amount,
            String currency,
            Instant transactionDate,
            String description,
            String key,
            String hash
    ){
        FinancialTransaction transaction =
                FinancialTransaction.create(
                        userId,
                        type,
                        amount,
                        currency,
                        TransactionSourceType.MANUAL,
                        transactionDate,
                        description,
                        userId,
                        CreatedByType.USER,
                        key,
                        hash,
                        null
                );

        return transactionRepository.saveAndFlush(transaction);
    }
    private String moneyText(BigDecimal value) {
        return value.setScale(4).toPlainString();
    }

    private FinancialTransactionDetailResponse complete(
            FinancialTransaction transaction,
            List<TransactionEntry> entries
    ){
        transaction.complete(Instant.now());

        List<TransactionEntry> savedEntries =
                transactionEntryRepository.saveAllAndFlush(entries);

        return transactionMapper.toDetail(
                transaction,
                savedEntries
        );
    }

    public FinancialTransactionDetailResponse openingBalance(
            String accountId,
            String idempotencyKey,
            OpeningBalanceCreationRequest request
    ){
        String userId = currentUserProvider.getUserId();
        String key = requireKey(idempotencyKey);

        String hash = requestHash(
                TransactionType.OPENING_BALANCE.name(),
                accountId,
                moneyText(request.amount()),
                request.transactionDate().toString(),
                normalizeDescription(request.description())
        );

        /// 1. Block the Account before checking the History
        Account account = lockOwnedAccount(accountId, userId);

        ///2. Retry Have to return old transaction before checking opening
        var existing = replay(userId, key, hash);

        if(existing.isPresent()){
            return existing.get();
        }

        ///3. Only declare for account haven't had history yet
        if(transactionEntryRepository.existsByAccountId(accountId)
            || account.getCurrentBalance().signum() != 0){
            throw new AppException(
                    LedgerErrorCode.OPENING_BALANCE_NOT_ALLOWED
            );
        }

        ///4. Create transaction
        FinancialTransaction transaction = createPending(
                userId,
                TransactionType.OPENING_BALANCE,
                request.amount(),
                account.getCurrency(),
                Instant.now(),
                request.description(),
                key,
                hash
        );

        /// 5. Entity check ACTIVE then plus balance
        BigDecimal balanceAfter = account.credit(request.amount());

        /// 6. Record changes - Ghi nhận thay đổi
        TransactionEntry entry = TransactionEntry.create(
                transaction.getId(),
                account.getId(),
                null,
                EntryDirection.CREDIT,
                request.amount(),
                balanceAfter,
                request.description()
        );

        return complete(transaction, List.of(entry));
    }

    public FinancialTransactionDetailResponse expense(
            String idempotencyKey,
            CashFlowCreationRequest request
    ){
        ///1. Get userId
        String userId = currentUserProvider.getUserId();

        /// 1.2. Check for Null Value base on Key
        String key = requireKey(idempotencyKey);

        ///2. Hash idempotencyKey
        String hash = requestHash(
                TransactionType.EXPENSE.name(),
                request.accountId(),
                request.categoryId(),
                moneyText(request.amount()),
                request.transactionDate().toString(),
                normalizeDescription(request.description())
        );

        ///3. Lock Account => Don't allow to update Account
        Account account = lockOwnedAccount(
                request.accountId(),
                userId
        );

        ///4. if exist => return response of existed entity
        var existing = replay(userId, key, hash);
        if(existing.isPresent())
        {
            return existing.get();
        }

        /// 4.2 If Category is not existed => return AppException
        Category category = categoryRepository
                .findUsableById(request.categoryId(), userId)
                .orElseThrow(() -> new AppException(
                        LedgerErrorCode.CATEGORY_NOT_FOUND
                ));

        ///5. if CategoryType is not EXPENSE => return AppException
        if(category.getType() != CategoryType.EXPENSE){
            throw new AppException(
                    LedgerErrorCode.CATEGORY_NOT_EXPENSE
            );
        }

        ///6. Create transaction with Pending Status
        FinancialTransaction transaction = createPending(
                userId,
                TransactionType.EXPENSE,
                request.amount(),
                account.getCurrency(),
                request.transactionDate(),
                request.description(),
                key,
                hash
        );

        BigDecimal balanceAfter = account.debit(request.amount());

        TransactionEntry entry = TransactionEntry.create(
                transaction.getId(),
                account.getId(),
                category.getId(),
                EntryDirection.DEBIT,
                request.amount(),
                balanceAfter,
                request.description()
        );

        return complete(transaction, List.of(entry));
    }

    public FinancialTransactionDetailResponse transfer(
            String idempotencyKey,
            TransferCreationRequest request
    ){
        String userId = currentUserProvider.getUserId();
        String key = requireKey(idempotencyKey);

        if(request.sourceAccountId().equals(
                request.destinationAccountId()
        )){
            throw new AppException(LedgerErrorCode.SAME_ACCOUNT_TRANSFER);
        }

        String hash = requestHash(
                TransactionType.TRANSFER.name(),
                request.sourceAccountId(),
                request.destinationAccountId(),
                moneyText(request.amount()),
                request.transactionDate().toString(),
                normalizeDescription(request.description())
        );

        /// Repository currently have ORDER BY account.id.
        List<Account> accounts = accountRepository
                .findAllOwnedForUpdate(
                        userId,
                        List.of(
                                request.sourceAccountId(),
                                request.destinationAccountId()
                        )
                );

        if(accounts.size() != 2){
            throw new AppException(
                    LedgerErrorCode.ACCOUNT_NOT_FOUND
            );
        }

        var existing = replay(userId, key, hash);

        if(existing.isPresent()){
            return existing.get();
        }

        Account source = accounts.stream()
                .filter(account -> account.getId().equals(
                        request.sourceAccountId()
                ))
                .findFirst()
                .orElseThrow();

        Account destination = accounts.stream()
                .filter(account -> account.getId().equals(
                        request.destinationAccountId()
                ))
                .findFirst()
                .orElseThrow();

        if(!source.getCurrency().equals(destination.getCurrency())){
            throw new AppException(
                    LedgerErrorCode.CURRENCY_MISMATCH
            );
        }

        FinancialTransaction transaction = createPending(
                userId,
                TransactionType.TRANSFER,
                request.amount(),
                source.getCurrency(),
                request.transactionDate(),
                request.description(),
                key,
                hash
        );

        BigDecimal sourceBalance = source.debit(request.amount());

        BigDecimal destinationBalance = destination.credit(request.amount());

        TransactionEntry debitEntry = TransactionEntry.create(
                transaction.getId(),
                source.getId(),
                null,
                EntryDirection.DEBIT,
                request.amount(),
                sourceBalance,
                request.description()
        );

        TransactionEntry creditEntry = TransactionEntry.create(
                transaction.getId(),
                destination.getId(),
                null,
                EntryDirection.CREDIT,
                request.amount(),
                destinationBalance,
                request.description()
        );

        return complete(
                transaction,
                List.of(debitEntry, creditEntry)
        );
    }

}
