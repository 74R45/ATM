package com.shahrai.atm.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;

public class Admin {
    @NotBlank private final String login;
    @NotBlank private final String password;

    // String login, String password

    public Admin(@JsonProperty("login") String login,
                 @JsonProperty("password") String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() { return login; }

    public String getPassword() { return password; }
}