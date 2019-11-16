package com.shahrai.atm.backend.service;

import com.shahrai.atm.backend.dao.UserDao;
import com.shahrai.atm.backend.exceptions.NotAcceptableException;
import com.shahrai.atm.backend.exceptions.NotFoundException;
import com.shahrai.atm.backend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final UserDao userDao;

    @Autowired
    public UserService(@Qualifier("postgresUser") UserDao userDao) {
        this.userDao = userDao;
    }

    public int login(User user) {
        Optional<User> dbUser = userDao.selectUserByLogin(user.getLogin());
        if (dbUser.isEmpty())
            throw new NotFoundException();

        if (dbUser.get().getPassword().equals(user.getPassword()))
            return 0;
        else
            throw new NotAcceptableException();
    }

    public Map<String, String> getLoginQuestion(String login) {
        Optional<User> dbUser = userDao.selectUserByLogin(login);
        if (dbUser.isEmpty())
            throw new NotFoundException();

        HashMap<String, String> res = new HashMap<>(1);
        res.put("question", dbUser.get().getQuestion());
        return res;
    }

    public int verifyLoginQuestion(User user) {
        Optional<User> dbUser = userDao.selectUserByLogin(user.getLogin());
        if (dbUser.isEmpty())
            throw new NotFoundException();

        if (dbUser.get().getAnswer().equals(user.getAnswer()))
            return 0;
        else
            throw new NotAcceptableException();
    }

    public Map<String, String> getUserByLogin(String login) {
//        if (token.itn != itn)
//            throw new ForbiddenException();

        Optional<User> dbUser = userDao.selectUserByLogin(login);
        if (dbUser.isEmpty())
            throw new NotFoundException();

        HashMap<String, String> res = new HashMap<>(4);
        res.put("itn", dbUser.get().getItn());
        res.put("name", dbUser.get().getName());
        res.put("surname", dbUser.get().getSurname());
        res.put("partonymic", dbUser.get().getPatronymic());
        return res;
    }
}
