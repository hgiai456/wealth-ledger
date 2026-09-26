package com.giaidev.ledger.repository;

import com.giaidev.ledger.entity.TransactionEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionEntryRepository
        extends JpaRepository<TransactionEntry, String> {

    List<TransactionEntry>
    findAllByTransactionIdOrderByCreatedAtAscIdAsc(
            String transactionId
    );

    Page<TransactionEntry>
    findAllByAccountIdOrderByCreatedAtDescIdDesc(
            String accountId,
            Pageable pageable
    );

    boolean existsByAccountId(String accountId);

//    IdDesc/IdAsc dùng để phân định thứ tự khi nhiều bản ghi cùng thời gian.
//    Nếu chỉ sort theo thời gian, các bản ghi bằng nhau có thể đổi vị trí giữa những lần truy vấn.
}
