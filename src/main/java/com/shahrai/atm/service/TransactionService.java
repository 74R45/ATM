package com.shahrai.atm.service;

import com.shahrai.atm.dao.AccountDao;
import com.shahrai.atm.dao.TransactionDao;
import com.shahrai.atm.exceptions.BadRequestException;
import com.shahrai.atm.exceptions.ForbiddenException;
import com.shahrai.atm.exceptions.NotFoundException;
import com.shahrai.atm.model.Account;
import com.shahrai.atm.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
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

    public List<Transaction> getTransactionsDuringPeriod(HttpServletRequest request, String number, Timestamp start, Timestamp end) {
        HttpSession session = request.getSession(false);
        if (session == null)
            throw new ForbiddenException();
        if (session.getCreationTime() < System.currentTimeMillis() - 300000L) {
            session.invalidate();
            throw new ForbiddenException("Session Expired");
        }
        if (!session.getAttribute("type").equals("user"))
            throw new ForbiddenException();

        if (start.after(end) || end.after(new Timestamp(System.currentTimeMillis())))
            throw new BadRequestException("Incorrect timestamps.");

        Optional<Account> acc = accountDao.selectAccountByNumber(number);
        if (acc.isPresent() && !session.getAttribute("itn").equals(acc.get().getItn())) {
            throw new ForbiddenException();
        }

        return transactionDao.selectTransactionsOnPeriod(number, start, end);
    }


    public Transaction makeTransaction(HttpServletRequest request, Transaction transaction) {
        HttpSession session = request.getSession(false);
        if (session == null)
            throw new ForbiddenException();
        if (session.getCreationTime() < System.currentTimeMillis() - 300000L) {
            session.invalidate();
            throw new ForbiddenException("Session Expired");
        }
        if (!session.getAttribute("type").equals("user"))
            throw new ForbiddenException();

        Account dbFrom;
        try {
            dbFrom = accountDao.selectAccountByNumber(transaction.getCardFrom()).get();
            if (dbFrom.isBlocked())
                throw new NoSuchElementException();
        } catch (NoSuchElementException e) {
            throw new NotFoundException("Card of the sender not found.");
        }

        if (!session.getAttribute("itn").equals(dbFrom.getItn())) {
            throw new ForbiddenException();
        }

        Account dbTo;
        try {
            dbTo = accountDao.selectAccountByNumber(transaction.getCardTo()).get();
            if (dbTo.isBlocked())
                throw new NoSuchElementException();
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
                dbFrom.getDeletionTime(),
                dbFrom.getAmount().subtract(transaction.getAmount()),
                dbFrom.getAmountCredit(),
                dbFrom.getCreditLimit(),
                dbFrom.getNextCreditTime(),
                dbFrom.getPin(),
                dbFrom.getAttemptsLeft()
        ));
        BigDecimal newAmountCredit, newAmount;
        Timestamp newNextCreditTime = dbTo.getNextCreditTime();
        if (dbTo.getAmountCredit().compareTo(transaction.getAmount()) >= 0) {
            newAmountCredit = dbTo.getAmountCredit().subtract(transaction.getAmount());
            newAmount = dbTo.getAmount();
            if (newNextCreditTime.before(new Timestamp(System.currentTimeMillis()))) {
                int months;
                for (months = 0; newNextCreditTime.getTime() + 2592000000L*months <= System.currentTimeMillis(); months++) {
                    newAmountCredit = newAmountCredit.multiply(new BigDecimal("1.02"));
                }
                newNextCreditTime = new Timestamp(newNextCreditTime.getTime() + 2592000000L*months);
            }
        } else {
            newAmountCredit = BigDecimal.ZERO;
            newAmount = dbTo.getAmount().add(transaction.getAmount().subtract(dbTo.getAmountCredit()));
            newNextCreditTime = null;
        }
        accountDao.updateAccountByNumber(dbTo.getNumber(), new Account(
                dbTo.getNumber(),
                dbTo.getItn(),
                dbTo.getExpiration(),
                dbTo.isCredit(),
                dbTo.isBlocked(),
                dbTo.getDeletionTime(),
                newAmount,
                newAmountCredit,
                dbTo.getCreditLimit(),
                newNextCreditTime,
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
