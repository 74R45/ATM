package com.shahrai.atm.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;

public class User {

    @NotBlank private final String itn;
    @NotBlank private final String name;
    @NotBlank private final String surname;
    @NotBlank private final String patronymic;
    @NotBlank private final String login;
    @NotBlank private final String password;
    @NotBlank private final String question;
    @NotBlank private final String answer;

    // int itn, String name, String surname, String patronymic, String login, String password, String question, String answer

    public User(@JsonProperty("itn") String itn,
                @JsonProperty("name") String name,
                @JsonProperty("surname") String surname,
                @JsonProperty("patronymic") String patronymic,
                @JsonProperty("login") String login,
                @JsonProperty("password") String password,
                @JsonProperty("question") String question,
                @JsonProperty("answer") String answer) {
        this.itn = itn;
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.login = login;
        this.password = password;
        this.question = question;
        this.answer = answer;
    }

    public String getItn() {
        return itn;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}