package com.shahrai.atm.service;

import com.shahrai.atm.dao.AdminDao;
import com.shahrai.atm.exceptions.NotAcceptableException;
import com.shahrai.atm.exceptions.NotFoundException;
import com.shahrai.atm.model.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Service
public class AdminService {

    private final AdminDao adminDao;

    @Autowired
    public AdminService(@Qualifier("postgresAdmin") AdminDao adminDao) {
        this.adminDao = adminDao;
    }

    public Map<String, Object> login(HttpServletRequest request, Admin admin) {
        String password = adminDao.selectPassword(admin.getLogin());
        if (password.equals(""))
            throw new NotFoundException();

        if (password.equals(admin.getPassword())) {
            HttpSession session = request.getSession();
            session.setAttribute("type", "admin");
            return Map.of("sessionToken", session.getId());
        } else
            throw new NotAcceptableException();
    }
}
