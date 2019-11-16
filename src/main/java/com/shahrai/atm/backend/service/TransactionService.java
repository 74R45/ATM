package com.shahrai.atm.backend.service;

import com.shahrai.atm.backend.dao.TransactionDao;
import com.shahrai.atm.backend.exceptions.BadRequestException;
import com.shahrai.atm.backend.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionDao transactionDao;

    @Autowired
    public TransactionService(@Qualifier("postgresTransaction") TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

    public List<Transaction> getTransactionsDuringPeriod(String number, Timestamp start, Timestamp end) {
//        if (token.number != number)
//            throw new ForbiddenException();

        if (start.after(end) || end.after(new Timestamp(System.currentTimeMillis())))
            throw new BadRequestException();

        return transactionDao.selectTransactionsOnPeriod(number, start, end);
    }
}
