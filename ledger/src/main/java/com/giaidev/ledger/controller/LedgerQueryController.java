package com.giaidev.ledger.controller;


import com.giaidev.core.dto.ApiResponse;
import com.giaidev.core.dto.PageResponse;
import com.giaidev.ledger.dto.response.FinancialTransactionDetailResponse;
import com.giaidev.ledger.dto.response.FinancialTransactionSummaryResponse;
import com.giaidev.ledger.dto.response.TransactionEntryResponse;
import com.giaidev.ledger.service.TransactionQueryService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ledger")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class LedgerQueryController {
    private final TransactionQueryService transactionQueryService;

    @GetMapping("/transactions")
    public ApiResponse<PageResponse<FinancialTransactionSummaryResponse>>
    getHistory(
            @RequestParam(name = "page", defaultValue = "0")
            @Min(0)
            int page,

            @RequestParam(name = "size", defaultValue = "20")
            @Min(1)
            @Max(100)
            int size
    ){
        return ApiResponse.success(
                transactionQueryService.getHistory(page, size)
        );
    }

    @GetMapping("/transactions/{transactionId}") ///Get Transaction Detail
    public ApiResponse<FinancialTransactionDetailResponse>
    getById(
            @PathVariable("transactionId") String transactionId
    ){
        return ApiResponse.success(
                transactionQueryService.getById(transactionId)
        );
    }


    @GetMapping("/accounts/{accountId}/entries")//Get
    public ApiResponse<PageResponse<TransactionEntryResponse>>
    getAccountEntries(
            @PathVariable("accountId") String accountId,

            @RequestParam(name = "page", defaultValue = "0")
            @Min(0)
            int page,

            @RequestParam(name = "size", defaultValue = "20")
            @Min(1)
            @Max(100)
            int size
    ){
        return ApiResponse.success(
          transactionQueryService.getAccountEntries(
                  accountId,
                  page,
                  size
          )
        );
    }



}
