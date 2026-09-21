package com.giaidev.ledger.mapper;

import com.giaidev.ledger.dto.response.FinancialTransactionDetailResponse;
import com.giaidev.ledger.dto.response.FinancialTransactionSummaryResponse;
import com.giaidev.ledger.entity.FinancialTransaction;
import com.giaidev.ledger.entity.TransactionEntry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = TransactionEntryMapper.class,
// cho phép MapStruct chuyển từng entry sang DTO.
// MapStruct hỗ trợ kết hợp nhiều tham số nguồn vào một DTO như trường hợp này.
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface FinancialTransactionMapper {
    FinancialTransactionSummaryResponse toSummary(
            FinancialTransaction transaction
    );

    @Mapping(target = "entries", source = "entries")
    FinancialTransactionDetailResponse toDetail(
            FinancialTransaction transaction,
            List<TransactionEntry> entries
    );


}
