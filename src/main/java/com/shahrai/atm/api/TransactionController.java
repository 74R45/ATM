package com.shahrai.atm.api;

import com.shahrai.atm.model.Transaction;
import com.shahrai.atm.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    @PostMapping
    public List<Transaction> getTransactionsDuringPeriod(HttpServletRequest request,
                                                         @RequestParam("number") String number,
                                                         @RequestParam("start") Timestamp start,
                                                         @RequestParam("end") Timestamp end) {
        return transactionService.getTransactionsDuringPeriod(request, number, start, end);
    }

    @PutMapping
    public Transaction makeTransaction(HttpServletRequest request, @Valid @RequestBody Transaction transaction) {
        return transactionService.makeTransaction(request, transaction);
    }
}