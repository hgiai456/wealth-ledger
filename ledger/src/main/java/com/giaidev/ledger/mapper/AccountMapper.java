package com.giaidev.ledger.mapper;

import com.giaidev.ledger.dto.response.AccountResponse;
import com.giaidev.ledger.entity.Account;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountResponse toResponse(Account account);

    List<AccountResponse> toResponse(List<Account> accounts);
}
