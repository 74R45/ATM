package com.shahrai.atm.backend.api;

import com.shahrai.atm.backend.model.Account;
import com.shahrai.atm.backend.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RequestMapping("api/v1/account")
@RestController
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping(path = "/login")
    public int login(@Valid @NotNull @RequestBody Account account) {
        return accountService.login(account);
    }
}