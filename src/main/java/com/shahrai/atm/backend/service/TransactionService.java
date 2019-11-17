package com.shahrai.atm.backend.service;

import com.shahrai.atm.backend.dao.AccountDao;
import com.shahrai.atm.backend.dao.TransactionDao;
import com.shahrai.atm.backend.exceptions.BadRequestException;
import com.shahrai.atm.backend.exceptions.NotFoundException;
import com.shahrai.atm.backend.model.Account;
import com.shahrai.atm.backend.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionDao transactionDao;
    private final AccountDao accountDao;

    @Autowired
    public TransactionService(@Qualifier("postgresTransaction") TransactionDao transactionDao,
                              @Qualifier("postgresAccount") AccountDao accountDao) {
        this.transactionDao = transactionDao;
        this.accountDao = accountDao;
    }

    public List<Transaction> getTransactionsDuringPeriod(String number, Timestamp start, Timestamp end) {
//        if (token.number != number)
//            throw new ForbiddenException();

        if (start.after(end) || end.after(new Timestamp(System.currentTimeMillis())))
            throw new BadRequestException();

        return transactionDao.selectTransactionsOnPeriod(number, start, end);
    }


    public Transaction makeTransaction(Transaction transaction) {
//        if (token.number != transaction.getCardFrom())
//            throw new ForbiddenException();

        Account dbFrom;
        try {
            dbFrom = accountDao.selectAccountByNumber(transaction.getCardFrom()).get();
        } catch (NoSuchElementException e) {
            throw new NotFoundException("Card of the sender not found.");
        }

        Account dbTo;
        try {
            dbTo = accountDao.selectAccountByNumber(transaction.getCardTo()).get();
        } catch (NoSuchElementException e) {
            throw new NotFoundException("Card of the recipient not found.");
        }

        if (dbFrom.getAmount().compareTo(transaction.getAmount()) < 0) {
            throw new BadRequestException("Not enough money on the card.");
        }

        accountDao.updateAccountByNumber(dbFrom.getNumber(), new Account(
                dbFrom.getNumber(),
                dbFrom.getItn(),
                dbFrom.getExpiration(),
                dbFrom.isCredit(),
                dbFrom.isBlocked(),
                dbFrom.getAmount().subtract(transaction.getAmount()),
                dbFrom.getAmountCredit(),
                dbFrom.getCreditLimit(),
                dbFrom.getPin(),
                dbFrom.getAttemptsLeft()
        ));
        BigDecimal newAmountCredit, newAmount;
        if (dbTo.getAmountCredit().compareTo(transaction.getAmount()) >= 0) {
            newAmountCredit = dbTo.getAmountCredit().subtract(transaction.getAmount());
            newAmount = dbTo.getAmount();
        } else {
            newAmountCredit = BigDecimal.ZERO;
            newAmount = dbTo.getAmount().add(transaction.getAmount().subtract(dbTo.getAmountCredit()));
        }
        accountDao.updateAccountByNumber(dbTo.getNumber(), new Account(
                dbTo.getNumber(),
                dbTo.getItn(),
                dbTo.getExpiration(),
                dbTo.isCredit(),
                dbTo.isBlocked(),
                newAmount,
                newAmountCredit,
                dbTo.getCreditLimit(),
                dbTo.getPin(),
                dbTo.getAttemptsLeft()
        ));

        Transaction completed = new Transaction(
                UUID.randomUUID(),
                transaction.getAmount(),
                new Timestamp(System.currentTimeMillis()),
                transaction.getCardFrom(),
                transaction.getCardTo());

        transactionDao.insertTransaction(completed);
        return completed;
    }
}
