package com.shahrai.atm.service;

import com.shahrai.atm.dao.UserDao;
import com.shahrai.atm.exceptions.ForbiddenException;
import com.shahrai.atm.exceptions.NotAcceptableException;
import com.shahrai.atm.exceptions.NotFoundException;
import com.shahrai.atm.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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

    public int login(HttpServletRequest request, User user) {
        Optional<User> dbUser = userDao.selectUserByLogin(user.getLogin());
        if (dbUser.isEmpty())
            throw new NotFoundException("User is not found.");

        if (dbUser.get().getPassword().equals(user.getPassword())) {
            HttpSession session = request.getSession();
            session.setAttribute("type", "user");
            session.setAttribute("itn", dbUser.get().getItn());
            return 0;
        } else
            throw new NotAcceptableException("Incorrect password.");
    }

    public Map<String, String> getLoginQuestion(String login) {
        Optional<User> dbUser = userDao.selectUserByLogin(login);
        if (dbUser.isEmpty())
            throw new NotFoundException("User is not found.");

        HashMap<String, String> res = new HashMap<>(1);
        res.put("question", dbUser.get().getQuestion());
        return res;
    }

    public int verifyLoginQuestion(HttpServletRequest request, User user) {
        Optional<User> dbUser = userDao.selectUserByLogin(user.getLogin());
        if (dbUser.isEmpty())
            throw new NotFoundException("User is not found.");

        if (dbUser.get().getAnswer().equals(user.getAnswer())) {
            HttpSession session = request.getSession();
            session.setAttribute("type", "user");
            session.setAttribute("itn", dbUser.get().getItn());
            return 0;
        } else
            throw new NotAcceptableException("Incorrect answer.");
    }

    public Map<String, String> getUserByLogin(HttpServletRequest request, String login) {
        HttpSession session = request.getSession(false);
        if (session == null)
            throw new ForbiddenException();
        if (session.getCreationTime() < System.currentTimeMillis() - 300000L) {
            session.invalidate();
            throw new ForbiddenException("Session Expired");
        }
        if (!session.getAttribute("type").equals("user"))
            throw new ForbiddenException();

        Optional<User> dbUser = userDao.selectUserByLogin(login);
        if (dbUser.isEmpty())
            throw new NotFoundException("User is not found.");

        if (!session.getAttribute("itn").equals(dbUser.get().getItn())) {
            throw new ForbiddenException();
        }

        HashMap<String, String> res = new HashMap<>(4);
        res.put("itn", dbUser.get().getItn());
        res.put("name", dbUser.get().getName());
        res.put("surname", dbUser.get().getSurname());
        res.put("partonymic", dbUser.get().getPatronymic());
        return res;
    }
}
