package com.shahrai.atm.api;

import com.shahrai.atm.model.Account;
import com.shahrai.atm.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    public Map<String, Object> login(HttpServletRequest request, @Valid @NotNull @RequestBody Account account) {
        return accountService.login(request, account);
    }

    @GetMapping(path = "/balance")
    public Map<String, BigDecimal> checkBalance(HttpServletRequest request, @RequestParam("number") String number) {
        return accountService.checkBalance(request, number);
    }

    @PostMapping(path = "/withdraw")
    public Map<String, Object> withdrawMoney(HttpServletRequest request, @RequestBody Account account) {
        return accountService.withdrawMoney(request, account);
    }

    @GetMapping(path = "all")
    public List<Map<String, Object>> getAccountsByItn(HttpServletRequest request, @RequestParam String itn) {
        return accountService.getAccountsByItn(request, itn);
    }

    @PutMapping
    public Map<String, Object> createAccount(HttpServletRequest request, @RequestParam("itn") String itn, @RequestParam("pin") String pin) {
        return accountService.createAccount(request, itn, pin);
    }

    @PostMapping(path = "/credit/activate")
    public int activateCredit(HttpServletRequest request, @RequestBody Account account) {
        return accountService.activateCredit(request, account);
    }

    @PostMapping(path = "/credit/deactivate")
    public int deactivateCredit(HttpServletRequest request, @RequestBody Account account) {
        return accountService.deactivateCredit(request, account);
    }

    @DeleteMapping(path = "/block")
    public int blockAccount(HttpServletRequest request, @RequestParam("number") String number) {
        return accountService.blockAccount(request, number);
    }

    @PostMapping(path = "/unblock")
    public int unblockAccount(HttpServletRequest request, @RequestParam("number") String number) {
        return accountService.unblockAccount(request, number);
    }

    @DeleteMapping(path = "/delete")
    public Map<String, Object> deleteAccount(HttpServletRequest request, @RequestParam("number") String number) {
        return accountService.deleteAccount(request, number);
    }
}