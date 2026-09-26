package com.giaidev.ledger.controller;

import com.giaidev.core.dto.ApiResponse;
import com.giaidev.ledger.dto.request.CashFlowCreationRequest;
import com.giaidev.ledger.dto.request.OpeningBalanceCreationRequest;
import com.giaidev.ledger.dto.request.TransferCreationRequest;
import com.giaidev.ledger.dto.response.FinancialTransactionDetailResponse;
import com.giaidev.ledger.service.TransactionCommandService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController ///  => @Controller @ResponseBody
@RequestMapping("/ledger")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class LedgerCommandController {
    private final TransactionCommandService service;

    @PostMapping("/accounts/{accountId}/opening-balance")
    public ApiResponse<FinancialTransactionDetailResponse> openingBalance(
            @PathVariable("accountId") String accountId,

            @RequestHeader("Idempotency-Key")
            @NotBlank @Size(max = 100)
            String idempotencyKey,

            @Valid @RequestBody
            OpeningBalanceCreationRequest request

    ){
        return ApiResponse.success(
                service.openingBalance(accountId, idempotencyKey, request)
        );
    }

    @PostMapping("/transactions/expense")
    public  ApiResponse<FinancialTransactionDetailResponse> expense(
            @RequestHeader("Idempotency-Key")
            @NotBlank @Size(max = 100)
            String key,

            @Valid @RequestBody CashFlowCreationRequest request
            ){
      return ApiResponse.success(
                service.expense(key, request)
        );
    }

    @PostMapping("/transactions/transfers")
    public ApiResponse<FinancialTransactionDetailResponse> transfer(
            @RequestHeader("Idempotency-Key")
            @NotBlank @Size(max = 100)
            String key,

            @Valid @RequestBody TransferCreationRequest request
    ) {
        return ApiResponse.success(service.transfer(key, request));
    }

}
