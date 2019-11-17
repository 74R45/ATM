package com.shahrai.atm.backend.api;

import com.shahrai.atm.backend.model.Transaction;
import com.shahrai.atm.backend.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.List;

@RequestMapping("api/v1/transaction")
@RestController
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public List<Transaction> getTransactionsDuringPeriod(@RequestParam("number") String number,
                                                         @RequestParam("start") Timestamp start,
                                                         @RequestParam("end") Timestamp end) {
        return transactionService.getTransactionsDuringPeriod(number, start, end);
    }

    @PutMapping
    public Transaction makeTransaction(@Valid @RequestBody Transaction transaction) {
        return transactionService.makeTransaction(transaction);
    }
}