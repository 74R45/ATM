package com.shahrai.atm.service;

import com.shahrai.atm.dao.AdminDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final AdminDao adminDao;

    @Autowired
    public AdminService(@Qualifier("postgresAdmin") AdminDao adminDao) {
        this.adminDao = adminDao;
    }
}
