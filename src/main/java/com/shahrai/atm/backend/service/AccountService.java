package com.shahrai.atm.backend.service;

import com.shahrai.atm.backend.dao.AccountDao;
import com.shahrai.atm.backend.exceptions.NotAcceptableException;
import com.shahrai.atm.backend.exceptions.NotFoundException;
import com.shahrai.atm.backend.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Optional;

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
}
