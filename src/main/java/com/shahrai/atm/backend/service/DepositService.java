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

        if (getDepositsByItn(deposit.getItn()).size() >= 2)
            throw new BadRequestException("Maximum number of deposits reached.");

        Optional<Account> dbAcc = accountDao.selectAccountsByItn(deposit.getItn()).stream()
                .filter(account -> account.getNumber().equals(number))
                .filter(account -> !account.isBlocked())
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
                dbAcc.get().getDeletionTime(),
                dbAcc.get().getAmount().subtract(deposit.getAmount()),
                dbAcc.get().getAmountCredit(),
                dbAcc.get().getCreditLimit(),
                dbAcc.get().getNextCreditTime(),
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
            long exp = deposit.getExpiration().getTime(),
                 now = System.currentTimeMillis();
            BigDecimal accrued;
            if (exp >= now) {
                accrued = BigDecimal.valueOf(31556952000L - (exp - now), 2)
                        .divide(BigDecimal.valueOf(31556952000L), RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("0.15"))
                        .multiply(deposit.getAmount());
            } else
                accrued = deposit.getAmount().multiply(new BigDecimal("0.15"));
            res.add(Map.of(
                    "id", deposit.getId(),
                    "expiration", deposit.getExpiration(),
                    "amount", deposit.getAmount(),
                    "accrued", accrued));
        }
        return res;
    }

    public int withdrawToAccount(UUID id, Account account) {
//        if (token.number != number)
//            throw new ForbiddenException();

        Optional<Deposit> dbDeposit = depositDao.selectDepositById(id);
        if (dbDeposit.isEmpty()) {
            throw new NotFoundException("Deposit not found.");
        }

        Optional<Account> maybeAcc = accountDao.selectAccountByNumber(account.getNumber());
        if (maybeAcc.isEmpty() || maybeAcc.get().isBlocked()) {
            throw new NotFoundException("Card not found.");
        }

        Account dbAcc = maybeAcc.get();
        BigDecimal amount;
        if (dbDeposit.get().getExpiration().after(new Timestamp(System.currentTimeMillis()))) {
            amount = dbDeposit.get().getAmount();
        } else {
            amount = dbDeposit.get().getAmount().multiply(new BigDecimal("1.15"));
        }

        BigDecimal newAmountCredit, newAmount;
        Timestamp newNextCreditTime = dbAcc.getNextCreditTime();
        if (dbAcc.getAmountCredit().compareTo(amount) >= 0) {
            newAmountCredit = dbAcc.getAmountCredit().subtract(amount);
            newAmount = dbAcc.getAmount();
            if (dbAcc.getNextCreditTime().before(new Timestamp(System.currentTimeMillis()))) {
                int months;
                for (months = 0; dbAcc.getNextCreditTime().getTime() + 2592000000L*months <= System.currentTimeMillis(); months++) {
                    newAmountCredit = newAmountCredit.multiply(new BigDecimal("1.02"));
                }
                newNextCreditTime = new Timestamp(dbAcc.getNextCreditTime().getTime() + 2592000000L*months);
            }
        } else {
            newAmountCredit = BigDecimal.ZERO;
            newAmount = dbAcc.getAmount().add(amount.subtract(dbAcc.getAmountCredit()));
            newNextCreditTime = null;
        }
        accountDao.updateAccountByNumber(dbAcc.getNumber(), new Account(
                dbAcc.getNumber(),
                dbAcc.getItn(),
                dbAcc.getExpiration(),
                dbAcc.isCredit(),
                dbAcc.isBlocked(),
                dbAcc.getDeletionTime(),
                newAmount,
                newAmountCredit,
                dbAcc.getCreditLimit(),
                newNextCreditTime,
                dbAcc.getPin(),
                dbAcc.getAttemptsLeft()
        ));

        return depositDao.deleteDepositById(id);
    }
}
