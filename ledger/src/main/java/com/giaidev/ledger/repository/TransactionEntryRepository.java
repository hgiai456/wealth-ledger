package com.giaidev.ledger.repository;

import com.giaidev.ledger.entity.TransactionEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionEntryRepository
        extends JpaRepository<TransactionEntry, String> {
    List<TransactionEntry> findAllByTransactionIdOrderByCreatedAtAsc(
            String transactionId
    );

    Page<TransactionEntry>
    findAllByAccountIdOrderByCreatedAtDesc(
            String accountId,
            Pageable pageable
    );
}
