package com.shahrai.atm.backend.service;

import com.shahrai.atm.backend.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserDao userDao;

    @Autowired
    public UserService(@Qualifier("postgresUser") UserDao userDao) {
        this.userDao = userDao;
    }
}
