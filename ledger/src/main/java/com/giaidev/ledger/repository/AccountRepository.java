package com.giaidev.ledger.repository;

import com.giaidev.ledger.entity.Account;
import com.giaidev.ledger.enums.AccountStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select account
        from Account account
        where account.id = :accountId
          and account.userId = :userId
        """)
    Optional<Account> findOwnedForUpdate(
            @Param("accountId") String accountId,
            @Param("userId") String userId
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select account
        from Account account
        where account.userId = :userId
          and account.id in :accountIds
        order by account.id
        """)
    List<Account> findAllOwnedForUpdate(
            @Param("userId") String userId,
            @Param("accountIds") Collection<String> accountIds
    );


}
