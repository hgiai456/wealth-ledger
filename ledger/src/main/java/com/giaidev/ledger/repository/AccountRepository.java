package com.giaidev.ledger.repository;

import com.giaidev.ledger.entity.Account;
import com.giaidev.ledger.enums.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {

    Optional<Account> findByIdAndUserId(String id, String userId);

    List<Account> findAllByUserIdOrderByCreatedAtDesc(String userId);

    List<Account> findAllByUserIdAndStatusOrderByCreatedAtDesc(String userId, AccountStatus status);

    boolean existsByUserIdAndNameIgnoreCase(String userId, String name);
    //    SELECT EXISTS (
    //            SELECT 1
    //                    FROM ledger.accounts
    //                    WHERE user_id = ?
    //                    AND lower(name) = lower(?)
    //);

    boolean existsByUserIdAndNameIgnoreCaseAndIdNot(
            String userId,
            String name,
            String id
    );
}
