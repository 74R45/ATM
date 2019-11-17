package com.shahrai.atm.backend.service;

import com.shahrai.atm.backend.dao.AccountDao;
import com.shahrai.atm.backend.dao.DepositDao;
import com.shahrai.atm.backend.exceptions.BadRequestException;
import com.shahrai.atm.backend.exceptions.NotFoundException;
import com.shahrai.atm.backend.model.Account;
import com.shahrai.atm.backend.model.Deposit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.*;

@Service
public class DepositService {

    private final DepositDao depositDao;
    private final AccountDao accountDao;

    @Autowired
    public DepositService(@Qualifier("postgresDeposit") DepositDao depositDao,
                          @Qualifier("postgresAccount") AccountDao accountDao) {
        this.depositDao = depositDao;
        this.accountDao = accountDao;
    }

    public int createDeposit(String number, Deposit deposit) {
//        if (token.itn != deposit.getItn())
//            throw new ForbiddenException();

        Optional<Account> dbAcc = accountDao.selectAccountsByItn(deposit.getItn()).stream()
                .filter(account -> account.getNumber().equals(number))
                .findFirst();

        if (dbAcc.isEmpty()) {
            throw new NotFoundException();
        }

        if (deposit.getAmount().compareTo(dbAcc.get().getAmount()) > 0) {
            throw new BadRequestException("Requested amount of money is greater than on the requested card.");
        }

        accountDao.updateAccountByNumber(dbAcc.get().getNumber(), new Account(
                dbAcc.get().getNumber(),
                dbAcc.get().getItn(),
                dbAcc.get().getExpiration(),
                dbAcc.get().isCredit(),
                dbAcc.get().isBlocked(),
                dbAcc.get().getAmount().subtract(deposit.getAmount()),
                dbAcc.get().getAmountCredit(),
                dbAcc.get().getCreditLimit(),
                dbAcc.get().getPin(),
                dbAcc.get().getAttemptsLeft()
        ));

        return depositDao.insertDeposit(new Deposit(
                UUID.randomUUID(),
                deposit.getItn(),
                new Timestamp(System.currentTimeMillis() + 31556952000L), // +1 year
                deposit.getAmount()
        ));
    }

    public List<Map<String, Object>> getDepositsByItn(String itn) {
//        if (token.itn != deposit.getItn())
//            throw new ForbiddenException();

        List<Map<String, Object>> res = new ArrayList<>();
        for (Deposit deposit : depositDao.selectDepositsByItn(itn)) {
            BigDecimal accrued = BigDecimal.valueOf(deposit.getExpiration().getTime() - 31556952000L)
                    .divide(BigDecimal.valueOf(31556952000L), RoundingMode.HALF_UP).add(BigDecimal.ONE)
                    .multiply(deposit.getAmount());
            res.add(Map.of(
                    "id", deposit.getId(),
                    "expiration", deposit.getExpiration(),
                    "amount", deposit.getAmount(),
                    "accrued", accrued));
        }
        return res;
    }
}
