package com.giaidev.ledger.controller;




import com.giaidev.core.dto.ApiResponse;
import com.giaidev.ledger.dto.request.AccountCreationRequest;
import com.giaidev.ledger.dto.request.AccountUpdateRequest;
import com.giaidev.ledger.dto.response.AccountResponse;
import com.giaidev.ledger.enums.AccountStatus;
import com.giaidev.ledger.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ledger/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ApiResponse<AccountResponse> create(
            @Valid @RequestBody AccountCreationRequest request
            ){
        AccountResponse response =  accountService.create(request);
        return ApiResponse.success(response);
    }

    @GetMapping("/my-accounts")
    public ApiResponse<List<AccountResponse>> getAll(
            @RequestParam(required = false)
            AccountStatus status
    ){
        List<AccountResponse> accounts = accountService.getAll(status);

        return ApiResponse.success(accounts);
    }


    @GetMapping("/{accountId}")
    public ApiResponse<AccountResponse> getById(@PathVariable String accountId){
        AccountResponse response = accountService.getById(accountId);

        return ApiResponse.success(response);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{accountId}")
    public ApiResponse<AccountResponse> update(
            @PathVariable String accountId,
            @Valid @RequestBody AccountUpdateRequest request
            ){
        AccountResponse accountResponse = accountService.update(
                accountId,
                request
        );

        return ApiResponse.success(accountResponse);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/close/{accountId}")
    public ApiResponse<AccountResponse> close(
            @PathVariable String accountId
    ){
        AccountResponse accountResponse = accountService.close(
                accountId
        );

        return ApiResponse.success(accountResponse);
    }
}
