package com.shahrai.atm.backend.api;

import com.shahrai.atm.backend.model.User;
import com.shahrai.atm.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public int login(@RequestBody User user) {
        return userService.login(user);
    }

    @GetMapping(path = "/login/question")
    public Map<String, String> getLoginQuestion(@RequestParam("login") String login) {
        return userService.getLoginQuestion(login);
    }

    @PostMapping(path = "/login/question")
    public int verifyLoginQuestion(@RequestBody User user) {
        return userService.verifyLoginQuestion(user);
    }

    @GetMapping
    public Map<String, String> getUserByLogin(@RequestParam String login) {
        return userService.getUserByLogin(login);
    }
}