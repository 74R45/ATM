package com.shahrai.atm.backend.service;

import com.shahrai.atm.backend.dao.AccountDao;
import com.shahrai.atm.backend.exceptions.BadRequestException;
import com.shahrai.atm.backend.exceptions.NotAcceptableException;
import com.shahrai.atm.backend.exceptions.NotFoundException;
import com.shahrai.atm.backend.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

@Service
public class AccountService {

    private final AccountDao accountDao;

    @Autowired
    public AccountService(@Qualifier("postgresAccount") AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public int login(Account account) {
        Optional<Account> dbAcc = accountDao.selectAccountByNumber(account.getNumber());
        if (dbAcc.isEmpty())
            throw new NotFoundException();

        if (dbAcc.get().getPin().equals(account.getPin()))
            return 0;
        else
            throw new NotAcceptableException();
    }

    public Map<String, BigDecimal> checkBalance(String number) {
//        if (token.number != number)
//            throw new ForbiddenException();

        Optional<Account> dbAcc = accountDao.selectAccountByNumber(number);
        if (dbAcc.isEmpty()) {
            throw new NotFoundException();
        }

        HashMap<String, BigDecimal> res = new HashMap<>(2);
        res.put("amount", dbAcc.get().getAmount().subtract(dbAcc.get().getAmountCredit()));
        res.put("creditLimit", dbAcc.get().getCreditLimit());
        return res;
    }

    public Map<String, Object> withdrawMoney(Account account) {
//        if (token.number != number)
//            throw new ForbiddenException();

        Optional<Account> dbAcc = accountDao.selectAccountByNumber(account.getNumber());
        if (dbAcc.isEmpty()) {
            throw new NotFoundException();
        }

        int dbReturned = 1;
        if (account.getAmount().compareTo(dbAcc.get().getAmount()) > 0) {
            if (dbAcc.get().isCredit()) {
                if (account.getAmount().compareTo(dbAcc.get().getAmount().add(
                        dbAcc.get().getCreditLimit()).subtract(dbAcc.get().getAmountCredit())) <= 0) {
                    BigDecimal amountCredit = dbAcc.get().getAmountCredit().add(account.getAmount()).subtract(dbAcc.get().getAmount());
                    Account newAccount = new Account(account.getNumber(), dbAcc.get().getItn(), dbAcc.get().getExpiration(),
                            dbAcc.get().isCredit(), dbAcc.get().isBlocked(), BigDecimal.ZERO, amountCredit, dbAcc.get().getCreditLimit(), dbAcc.get().getPin());
                    dbReturned = accountDao.updateAccountByNumber(account.getNumber(), newAccount);
                } else {
                    throw new BadRequestException();
                }
            } else
                throw new BadRequestException();
        } else {
            Account newAccount = new Account(account.getNumber(), dbAcc.get().getItn(), dbAcc.get().getExpiration(),
                    dbAcc.get().isCredit(), dbAcc.get().isBlocked(),
                    dbAcc.get().getAmount().subtract(account.getAmount()),
                    dbAcc.get().getAmountCredit(), dbAcc.get().getCreditLimit(), dbAcc.get().getPin());
            dbReturned = accountDao.updateAccountByNumber(account.getNumber(), newAccount);
        }
        BigDecimal amountLeft = checkBalance(account.getNumber()).get("amount");

        HashMap<String, Object> res = new HashMap<>(4);
        res.put("amount", account.getAmount());
        res.put("timestamp", new Timestamp(System.currentTimeMillis()));
        res.put("number", account.getNumber());
        res.put("amountLeft", amountLeft);
        return res;
    }

    public List<Map<String, Object>> getAccountsByItn(String itn) {
//        if (token.itn != itn)
//            throw new ForbiddenException();

        List<Account> accs = accountDao.selectAccountsByItn(itn);

        List<Map<String, Object>> res = new ArrayList<>();
        for (Account acc : accs) {
            if (!acc.isBlocked()) {
                res.add(Map.of(
                        "number", acc.getNumber(),
                        "expiration", acc.getExpiration(),
                        "isCredit", acc.isCredit(),
                        "amount", acc.getAmount(),
                        "amountCredit", acc.getAmountCredit(),
                        "creditLimit", acc.getCreditLimit()));
            }
        }
        return res;
    }

    public Map<String, Object> createAccount(String itn, String pin) {
//        if (token.itn != itn)
//            throw new ForbiddenException();

        Account acc = new Account(
                new CreditCardNumberGenerator().generate("7474", 16),
                itn,
                new Timestamp(System.currentTimeMillis() + 94670856000L), // now + 3 years
                false,
                false,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                pin);

        if (accountDao.insertAccount(acc) == -1)
            throw new NotFoundException();

        return Map.of(
                "number", acc.getNumber(),
                "expiration", acc.getExpiration(),
                "isCredit", acc.isCredit(),
                "amount", acc.getAmount(),
                "amountCredit", acc.getAmountCredit(),
                "creditLimit", acc.getCreditLimit());
    }
}
