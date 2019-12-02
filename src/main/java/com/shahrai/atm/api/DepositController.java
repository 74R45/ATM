package com.shahrai.atm.api;

import com.shahrai.atm.model.Account;
import com.shahrai.atm.model.Deposit;
import com.shahrai.atm.service.DepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequestMapping("api/v1/deposit")
@RestController
public class DepositController {

    private final DepositService depositService;

    @Autowired
    public DepositController(DepositService depositService) {
        this.depositService = depositService;
    }

    @PutMapping
    public int createDeposit(HttpServletRequest request, @RequestParam("number") String number, @RequestBody Deposit deposit) {
        return depositService.createDeposit(request, number, deposit);
    }

    @GetMapping(path = "/all")
    public List<Map<String, Object>> getDepositsByItn(HttpServletRequest request, @RequestParam("itn") String itn) {
        return depositService.getDepositsByItn(request, itn);
    }

    @DeleteMapping
    public int withdrawToAccount(HttpServletRequest request, @RequestParam("id") UUID id, @RequestBody Account account) {
        return depositService.withdrawToAccount(request, id, account);
    }
}