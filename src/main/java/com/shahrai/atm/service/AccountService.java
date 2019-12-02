package com.shahrai.atm.service;

import com.shahrai.atm.dao.AccountDao;
import com.shahrai.atm.exceptions.BadRequestException;
import com.shahrai.atm.exceptions.ForbiddenException;
import com.shahrai.atm.exceptions.NotFoundException;
import com.shahrai.atm.util.CreditCardNumberGenerator;
import com.shahrai.atm.util.Sha256;
import com.shahrai.atm.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final AccountDao accountDao;

    @Autowired
    public AccountService(@Qualifier("postgresAccount") AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public Map<String, Object> login(HttpServletRequest request, Account account) {
        Optional<Account> dbAcc = accountDao.selectAccountByNumber(account.getNumber());
        if (dbAcc.isEmpty())
            throw new NotFoundException("Account is not found.");
        if (dbAcc.get().isBlocked()) {
            updateBlocked(dbAcc.get());
            throw new NotFoundException("Account is not found.");
        }

        HashMap<String, Object> res = new HashMap<>();

        if (dbAcc.get().getPin().equals(account.getPin())) {
            accountDao.updateAccountAttemptsLeftByNumber(dbAcc.get().getNumber(), 3);
            HttpSession session = request.getSession();
            session.setAttribute("type", "atm");
            session.setAttribute("number", account.getNumber());
            return res;
        } else {
            int attempts = dbAcc.get().getAttemptsLeft()-1;
            if (attempts > 0) {
                accountDao.updateAccountAttemptsLeftByNumber(dbAcc.get().getNumber(), dbAcc.get().getAttemptsLeft() - 1);
                res.put("attemptsLeft", attempts);
                return res;
            } else {
                doBlockAccount(dbAcc.get().getNumber());
                res.put("attemptsLeft", 0);
                return res;
            }
        }
    }

    public Map<String, BigDecimal> checkBalance(HttpServletRequest request, String number) {
        HttpSession session = request.getSession(false);
        if (session == null)
            throw new ForbiddenException();
        if (session.getCreationTime() < System.currentTimeMillis() - 300000L) {
            session.invalidate();
            throw new ForbiddenException("Session Expired");
        }
        if (!session.getAttribute("type").equals("atm")
                || !session.getAttribute("number").equals(number))
            throw new ForbiddenException();

        Optional<Account> maybeAcc = accountDao.selectAccountByNumber(number);
        if (maybeAcc.isEmpty()) {
            throw new NotFoundException("Account is not found.");
        }
        if (maybeAcc.get().isBlocked()) {
            updateBlocked(maybeAcc.get());
            throw new NotFoundException("Accont is not found.");
        }

        Account dbAcc = updateCredit(maybeAcc.get());
        HashMap<String, BigDecimal> res = new HashMap<>(2);
        res.put("amount", dbAcc.getAmount().subtract(dbAcc.getAmountCredit()));
        res.put("creditLimit", dbAcc.getCreditLimit());
        return res;
    }

    public Map<String, Object> withdrawMoney(HttpServletRequest request, Account account) {
        HttpSession session = request.getSession(false);
        if (session == null)
            throw new ForbiddenException();
        if (session.getCreationTime() < System.currentTimeMillis() - 300000L) {
            session.invalidate();
            throw new ForbiddenException("Session Expired");
        }
        if (!session.getAttribute("type").equals("atm")
                || !session.getAttribute("number").equals(account.getNumber()))
            throw new ForbiddenException();

        Optional<Account> maybeAcc = accountDao.selectAccountByNumber(account.getNumber());
        if (maybeAcc.isEmpty() || maybeAcc.get().isBlocked()) {
            throw new NotFoundException("Account is not found.");
        }
        Account dbAcc = updateCredit(maybeAcc.get());

        int dbReturned = 1;
        if (account.getAmount().compareTo(dbAcc.getAmount()) > 0) {
            if (dbAcc.isCredit()) {
                if (account.getAmount().compareTo(dbAcc.getAmount().add(
                        dbAcc.getCreditLimit()).subtract(dbAcc.getAmountCredit())) <= 0) {
                    BigDecimal amountCredit = dbAcc.getAmountCredit().add(account.getAmount()).subtract(dbAcc.getAmount());
                    Timestamp nextCreditTime = (dbAcc.getNextCreditTime() == null)
                            ? new Timestamp(System.currentTimeMillis() + 2592000000L)
                            : dbAcc.getNextCreditTime();
                    Account newAccount = new Account(account.getNumber(), dbAcc.getItn(), dbAcc.getExpiration(),
                            dbAcc.isCredit(), dbAcc.isBlocked(), dbAcc.getDeletionTime(), BigDecimal.ZERO,
                            amountCredit, dbAcc.getCreditLimit(), nextCreditTime, dbAcc.getPin(), 3);
                    dbReturned = accountDao.updateAccountByNumber(account.getNumber(), newAccount);
                } else {
                    throw new BadRequestException("Not enough money.");
                }
            } else
                throw new BadRequestException("Not enough money.");
        } else {
            Account newAccount = new Account(account.getNumber(), dbAcc.getItn(), dbAcc.getExpiration(),
                    dbAcc.isCredit(), dbAcc.isBlocked(), dbAcc.getDeletionTime(),
                    dbAcc.getAmount().subtract(account.getAmount()), dbAcc.getAmountCredit(),
                    dbAcc.getCreditLimit(), dbAcc.getNextCreditTime(), dbAcc.getPin(), 3);
            dbReturned = accountDao.updateAccountByNumber(account.getNumber(), newAccount);
        }
        BigDecimal amountLeft = checkBalance(request, account.getNumber()).get("amount");

        HashMap<String, Object> res = new HashMap<>(4);
        res.put("amount", account.getAmount());
        res.put("timestamp", new Timestamp(System.currentTimeMillis()));
        res.put("number", account.getNumber());
        res.put("amountLeft", amountLeft);
        return res;
    }

    public List<Map<String, Object>> getAccountsByItn(HttpServletRequest request, String itn) {
        HttpSession session = request.getSession(false);
        if (session == null)
            throw new ForbiddenException();
        if (session.getCreationTime() < System.currentTimeMillis() - 300000L) {
            session.invalidate();
            throw new ForbiddenException("Session Expired");
        }
        if (!(session.getAttribute("type").equals("user")
                && session.getAttribute("itn").equals(itn)) && !session.getAttribute("type").equals("admin"))
            throw new ForbiddenException();

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
                        "creditLimit", acc.getCreditLimit(),
                        "attemptsLeft", acc.getAttemptsLeft()));
            } else {
                updateBlocked(acc);
            }
        }
        return res;
    }

    public Map<String, Object> createAccount(HttpServletRequest request, String itn, String pin) {
        HttpSession session = request.getSession(false);
        if (session == null)
            throw new ForbiddenException();
        if (session.getCreationTime() < System.currentTimeMillis() - 300000L) {
            session.invalidate();
            throw new ForbiddenException("Session Expired");
        }
        if (!session.getAttribute("type").equals("admin"))
            throw new ForbiddenException();

        String number;
        do {
            number = new CreditCardNumberGenerator().generate("7474", 16);
        } while (accountDao.selectAccountByNumber(number).isPresent());

        Account acc = new Account(
                number,
                itn,
                new Timestamp(System.currentTimeMillis() + 94670856000L), // now + 3 years
                false,
                false,
                null,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                null,
                pin,
                3);

        if (accountDao.insertAccount(acc) == -1)
            throw new NotFoundException("User with this ITN was not found.");

        return Map.of(
                "number", acc.getNumber(),
                "expiration", acc.getExpiration(),
                "isCredit", acc.isCredit(),
                "amount", acc.getAmount(),
                "amountCredit", acc.getAmountCredit(),
                "creditLimit", acc.getCreditLimit());
    }

    public int activateCredit(HttpServletRequest request, Account account) {
        HttpSession session = request.getSession(false);
        if (session == null)
            throw new ForbiddenException();
        if (session.getCreationTime() < System.currentTimeMillis() - 300000L) {
            session.invalidate();
            throw new ForbiddenException("Session Expired");
        }
        if (!session.getAttribute("type").equals("user"))
            throw new ForbiddenException();

        Optional<Account> dbAcc = accountDao.selectAccountByNumber(account.getNumber());
        if (dbAcc.isEmpty()) {
            throw new NotFoundException("Account is not found.");
        }
        if (dbAcc.get().isBlocked()) {
            updateBlocked(dbAcc.get());
            throw new NotFoundException("Account is not found.");
        }

        if (!session.getAttribute("itn").equals(dbAcc.get().getItn())) {
            throw new ForbiddenException();
        }

        if (account.getCreditLimit().compareTo(BigDecimal.valueOf(5000)) > 0)
            throw new BadRequestException("The chosen limit is greater than 5000.00.");

        if (dbAcc.get().isCredit()) {
            updateCredit(dbAcc.get());
            throw new BadRequestException("This card is already a credit card.");
        }

        List<Map<String, Object>> accs = getAccountsByItn(request, dbAcc.get().getItn());
        for (Map<String, Object> acc : accs) {
            if (acc.get("isCredit") == Boolean.TRUE) {
                throw new BadRequestException("The user already has a credit card.");
            }
        }

        return accountDao.updateAccountByNumber(account.getNumber(), new Account(
                dbAcc.get().getNumber(),
                dbAcc.get().getItn(),
                dbAcc.get().getExpiration(),
                true,
                dbAcc.get().isBlocked(),
                dbAcc.get().getDeletionTime(),
                dbAcc.get().getAmount(),
                dbAcc.get().getAmountCredit(),
                account.getCreditLimit(),
                new Timestamp(System.currentTimeMillis() + 2592000000L),
                dbAcc.get().getPin(),
                dbAcc.get().getAttemptsLeft()
        ));
    }

    public int deactivateCredit(HttpServletRequest request, Account account) {
        HttpSession session = request.getSession(false);
        if (session == null)
            throw new ForbiddenException();
        if (session.getCreationTime() < System.currentTimeMillis() - 300000L) {
            session.invalidate();
            throw new ForbiddenException("Session Expired");
        }
        if (!session.getAttribute("type").equals("user"))
            throw new ForbiddenException();

        Optional<Account> dbAcc = accountDao.selectAccountByNumber(account.getNumber());
        if (dbAcc.isEmpty())
            throw new NotFoundException("Account is not found.");
        if (dbAcc.get().isBlocked()) {
            updateBlocked(dbAcc.get());
            throw new NotFoundException("Account is not found.");
        }
        if (!session.getAttribute("itn").equals(dbAcc.get().getItn()))
            throw new ForbiddenException();

        if (!dbAcc.get().isCredit())
            throw new BadRequestException("This card is already not a credit card.");

        if (dbAcc.get().getAmountCredit().subtract(dbAcc.get().getAmount()).compareTo(BigDecimal.ZERO) > 0) {
            updateCredit(dbAcc.get());
            throw new BadRequestException("There's still money on the credit.");
        }

        return accountDao.updateAccountByNumber(account.getNumber(), new Account(
                dbAcc.get().getNumber(),
                dbAcc.get().getItn(),
                dbAcc.get().getExpiration(),
                false,
                dbAcc.get().isBlocked(),
                dbAcc.get().getDeletionTime(),
                dbAcc.get().getAmount().subtract(dbAcc.get().getAmountCredit()),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                null,
                dbAcc.get().getPin(),
                dbAcc.get().getAttemptsLeft()
        ));
    }

    public int blockAccount(HttpServletRequest request, String number) {
        HttpSession session = request.getSession(false);
        if (session == null)
            throw new ForbiddenException();
        if (session.getCreationTime() < System.currentTimeMillis() - 300000L) {
            session.invalidate();
            throw new ForbiddenException("Session Expired");
        }
        if (!session.getAttribute("type").equals("admin"))
            throw new ForbiddenException();

        return doBlockAccount(number);
    }

    private int doBlockAccount(String number) {
        Optional<Account> maybeAcc = accountDao.selectAccountByNumber(number);
        if (maybeAcc.isEmpty())
            throw new NotFoundException("Account is not found.");
        if (maybeAcc.get().isBlocked()) {
            updateBlocked(maybeAcc.get());
            throw new NotFoundException("Account is not found.");
        }

        Account dbAcc = updateCredit(maybeAcc.get());
        return accountDao.updateAccountByNumber(number, new Account(
                dbAcc.getNumber(),
                dbAcc.getItn(),
                dbAcc.getExpiration(),
                dbAcc.isCredit(),
                true,
                new Timestamp(System.currentTimeMillis() + 86400000), // 24 hours from now
                dbAcc.getAmount(),
                dbAcc.getAmountCredit(),
                dbAcc.getCreditLimit(),
                dbAcc.getNextCreditTime(),
                dbAcc.getPin(),
                dbAcc.getAttemptsLeft()
        ));
    }

    public int unblockAccount(HttpServletRequest request, String number) {
        HttpSession session = request.getSession(false);
        if (session == null)
            throw new ForbiddenException();
        if (session.getCreationTime() < System.currentTimeMillis() - 300000L) {
            session.invalidate();
            throw new ForbiddenException("Session Expired");
        }
        if (!session.getAttribute("type").equals("admin"))
            throw new ForbiddenException();

        Optional<Account> dbAcc = accountDao.selectAccountByNumber(number);
        if (dbAcc.isEmpty())
            throw new NotFoundException("Account is not found.");
        if (!dbAcc.get().isBlocked()) {
            throw new BadRequestException("Card is not blocked.");
        }

        return accountDao.updateAccountByNumber(number, new Account(
                dbAcc.get().getNumber(),
                dbAcc.get().getItn(),
                dbAcc.get().getExpiration(),
                dbAcc.get().isCredit(),
                false,
                null,
                dbAcc.get().getAmount(),
                dbAcc.get().getAmountCredit(),
                dbAcc.get().getCreditLimit(),
                dbAcc.get().getNextCreditTime(),
                dbAcc.get().getPin(),
                dbAcc.get().getAttemptsLeft()
        ));
    }

    public Map<String, Object> deleteAccount(HttpServletRequest request, String number) {
        HttpSession session = request.getSession(false);
        if (session == null)
            throw new ForbiddenException();
        if (session.getCreationTime() < System.currentTimeMillis() - 300000L) {
            session.invalidate();
            throw new ForbiddenException("Session Expired");
        }
        if (!session.getAttribute("type").equals("admin"))
            throw new ForbiddenException();

        return doDeleteAccount(number);
    }

    private Map<String, Object> doDeleteAccount(String number) {
        Optional<Account> maybeAcc = accountDao.selectAccountByNumber(number);
        if (maybeAcc.isEmpty())
            throw new NotFoundException("Account is not found.");
        Account dbAcc = updateCredit(maybeAcc.get());

        accountDao.deleteAccountByNumber(number);

        List<Account> dbAccs = accountDao.selectAccountsByItn(dbAcc.getItn()).stream()
                .filter(account -> !account.getNumber().equals(number) &&
                        !account.isCredit() &&
                        !account.isBlocked())
                .collect(Collectors.toList());

        Account newAcc;
        if (!dbAccs.isEmpty()) {
            Account chosen = dbAccs.get(0);

            // computing the resulting amount of money
            BigDecimal newAmount, newAmountCredit, newCreditLimit;
            boolean isCredit;
            Timestamp nextCreditTime;
            newAmount = chosen.getAmount().add(dbAcc.getAmount());
            if (dbAcc.isCredit()) {
                if (newAmount.compareTo(dbAcc.getAmountCredit()) >= 0) {
                    newAmount = newAmount.subtract(dbAcc.getAmountCredit());
                    newAmountCredit = BigDecimal.ZERO;
                    newCreditLimit = BigDecimal.ZERO;
                    isCredit = false;
                    nextCreditTime = null;
                } else {
                    newAmountCredit = dbAcc.getAmountCredit().subtract(newAmount);
                    newAmount = BigDecimal.ZERO;
                    newCreditLimit = dbAcc.getCreditLimit();
                    isCredit = true;
                    nextCreditTime = new Timestamp(System.currentTimeMillis() + 2592000000L); // + 1 month
                }
            } else {
                newCreditLimit = BigDecimal.ZERO;
                newAmountCredit = BigDecimal.ZERO;
                isCredit = false;
                nextCreditTime = null;
            }

            newAcc = new Account(
                    chosen.getNumber(),
                    chosen.getItn(),
                    chosen.getExpiration(),
                    isCredit,
                    false,
                    null,
                    newAmount,
                    newAmountCredit,
                    newCreditLimit,
                    nextCreditTime,
                    chosen.getPin(),
                    chosen.getAttemptsLeft()
            );
            accountDao.updateAccountByNumber(chosen.getNumber(), newAcc);
        } else {
            //String newPin = generateRandomPin();
            newAcc = new Account(
                    new CreditCardNumberGenerator().generate("7474", 16),
                    dbAcc.getItn(),
                    new Timestamp(System.currentTimeMillis() + 94670856000L), // now + 3 years
                    dbAcc.isCredit(),
                    false,
                    null,
                    dbAcc.getAmount(),
                    dbAcc.getAmountCredit(),
                    dbAcc.getCreditLimit(),
                    dbAcc.getNextCreditTime(),
                    Sha256.hash("1111"),
                    3);
            accountDao.insertAccount(newAcc);
        }
        return Map.of(
                "number", newAcc.getNumber()
        );
    }

    private String generateRandomPin() {
        StringBuilder builder = new StringBuilder();
        String num = Integer.toString(new Random(System.currentTimeMillis()).nextInt(10000));
        return builder.append("0".repeat(4 - num.length())).append(num).toString();
    }

    private Account updateCredit(Account acc) {
        if (!acc.isCredit() || acc.getNextCreditTime().after(new Timestamp(System.currentTimeMillis())))
            return acc;

        Account newAcc;
        if (acc.getAmount().compareTo(acc.getAmountCredit()) >= 0) {
            newAcc = new Account(
                    acc.getNumber(),
                    acc.getItn(),
                    acc.getExpiration(),
                    true,
                    acc.isBlocked(),
                    acc.getDeletionTime(),
                    acc.getAmount().subtract(acc.getAmountCredit()),
                    BigDecimal.ZERO,
                    acc.getCreditLimit(),
                    null,
                    acc.getPin(),
                    acc.getAttemptsLeft()
            );
        } else {
            BigDecimal newAmountCredit = acc.getAmountCredit().subtract(acc.getAmount());
            int months;
            for (months = 0; acc.getNextCreditTime().getTime() + 2592000000L*months <= System.currentTimeMillis(); months++) {
                newAmountCredit = newAmountCredit.multiply(new BigDecimal("1.02"));
            }

            newAcc = new Account(
                    acc.getNumber(),
                    acc.getItn(),
                    acc.getExpiration(),
                    true,
                    acc.isBlocked(),
                    acc.getDeletionTime(),
                    BigDecimal.ZERO,
                    newAmountCredit,
                    acc.getCreditLimit(),
                    new Timestamp(acc.getNextCreditTime().getTime() + 2592000000L*months), // + 1 month
                    acc.getPin(),
                    acc.getAttemptsLeft()
            );
        }
        accountDao.updateAccountByNumber(acc.getNumber(), newAcc);
        return newAcc;
    }

    private void updateBlocked(Account acc) {
        if (acc.isBlocked() && acc.getDeletionTime().before(new Timestamp(System.currentTimeMillis())))
            doDeleteAccount(acc.getNumber());
    }
}