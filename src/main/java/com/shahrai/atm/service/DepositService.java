package com.shahrai.atm.service;

import com.shahrai.atm.dao.DepositDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class DepositService {

    private final DepositDao depositDao;

    @Autowired
    public DepositService(@Qualifier("postgresDeposit") DepositDao depositDao) {
        this.depositDao = depositDao;
    }
}
