package com.shahrai.atm.backend.service;

import com.shahrai.atm.backend.dao.AccountDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final AccountDao accountDao;

    @Autowired
    public AccountService(@Qualifier("postgresAccount") AccountDao accountDao) {
        this.accountDao = accountDao;
    }
}
