package com.giaidev.ledger.mapper;

import com.giaidev.ledger.dto.response.TransactionEntryResponse;
import com.giaidev.ledger.entity.TransactionEntry;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR
)//giúp build báo lỗi khi DTO có thêm field mà mapper chưa biết lấy giá trị từ đâu.
public interface TransactionEntryMapper {
    TransactionEntryResponse toResponse(TransactionEntry entry);

    List<TransactionEntryResponse> toResponse(
            List<TransactionEntry> entries
    );
}

