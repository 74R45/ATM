package com.shahrai.atm.backend.api;

import com.shahrai.atm.backend.model.Account;
import com.shahrai.atm.backend.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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

    @GetMapping(path = "/balance")
    public Map<String, BigDecimal> checkBalance(@RequestParam("number") String number) {
        return accountService.checkBalance(number);
    }

    @PostMapping(path = "/withdraw")
    public Map<String, Object> withdrawMoney(@RequestBody Account account) {
        return accountService.withdrawMoney(account);
    }

    @GetMapping(path = "all")
    public List<Map<String, Object>> getAccountsByItn(@RequestParam String itn) {
        return accountService.getAccountsByItn(itn);
    }

    @PutMapping
    public Map<String, Object> createAccount(@RequestParam("itn") String itn, @RequestParam("pin") String pin) {
        return accountService.createAccount(itn, pin);
    }

    @PostMapping(path = "/credit/activate")
    public int activateCredit(@RequestBody Account account) {
        return accountService.activateCredit(account);
    }

    @PostMapping(path = "/credit/deactivate")
    public int deactivateCredit(@RequestBody Account account) {
        return accountService.deactivateCredit(account);
    }
}