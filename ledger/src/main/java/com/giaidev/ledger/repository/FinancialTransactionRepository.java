package com.giaidev.ledger.repository;

import com.giaidev.ledger.entity.FinancialTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface FinancialTransactionRepository extends JpaRepository<FinancialTransaction, String> {
    Optional<FinancialTransaction> findByIdAndUserId(
            String id,
            String userId
    );

    Optional<FinancialTransaction> findByUserIdAndIdempotencyKey(
            String userId,
            String idempotencyKey
    );

    Page<FinancialTransaction>
    findAllByUserIdOrderByTransactionDateDesc(
            String userId,
            Pageable pageable
    );

    boolean existsByReversalOfTransactionId(
            String transactionId
    );


}
