package com.shahrai.atm.backend.service;

import com.shahrai.atm.backend.dao.AdminDao;
import com.shahrai.atm.backend.exceptions.NotAcceptableException;
import com.shahrai.atm.backend.exceptions.NotFoundException;
import com.shahrai.atm.backend.model.Admin;
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

    public int login(Admin admin) {
        String password = adminDao.selectPassword(admin.getLogin());
        if (password.equals(""))
            throw new NotFoundException();

        if (password.equals(admin.getPassword()))
            return 0;
        else
            throw new NotAcceptableException();
    }
}
