package com.shahrai.atm.service;

import com.shahrai.atm.dao.TransactionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private final TransactionDao transactionDao;

    @Autowired
    public TransactionService(@Qualifier("postgresTransaction") TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }
}
