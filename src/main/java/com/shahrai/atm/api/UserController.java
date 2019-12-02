package com.shahrai.atm.api;

import com.shahrai.atm.model.User;
import com.shahrai.atm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RequestMapping("api/v1/user")
@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/login")
    public Map<String, Object> login(HttpServletRequest request, @RequestBody User user) {
        return userService.login(request, user);
    }

    @PostMapping(path = "/login/question")
    public Map<String, String> getLoginQuestion(@RequestParam("login") String login) {
        return userService.getLoginQuestion(login);
    }

    @PostMapping(path = "/login/question/verify")
    public int verifyLoginQuestion(HttpServletRequest request, @RequestBody User user) {
        return userService.verifyLoginQuestion(request, user);
    }

    @PostMapping
    public Map<String, String> getUserByLogin(HttpServletRequest request, @RequestParam String login) {
        return userService.getUserByLogin(request, login);
    }
}