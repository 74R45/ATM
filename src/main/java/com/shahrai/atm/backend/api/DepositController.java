package com.shahrai.atm.backend.api;

import com.shahrai.atm.backend.model.Deposit;
import com.shahrai.atm.backend.service.DepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("api/v1/deposit")
@RestController
public class DepositController {

    private final DepositService depositService;

    @Autowired
    public DepositController(DepositService depositService) {
        this.depositService = depositService;
    }

    @PutMapping
    public int createDeposit(@RequestParam("number") String number, @RequestBody Deposit deposit) {
        return depositService.createDeposit(number, deposit);
    }

    @GetMapping(path = "/all")
    public List<Map<String, Object>> getDepositsByItn(@RequestParam("itn") String itn) {
        return depositService.getDepositsByItn(itn);
    }
}