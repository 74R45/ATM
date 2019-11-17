package com.shahrai.atm.backend.exceptions;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class AtmLoginException extends RuntimeException {
    private final int attemptsLeft;

    public AtmLoginException(@JsonProperty("attemptsLeft") int attemptsLeft) {

        this.attemptsLeft = attemptsLeft;
    }

    public int getAttemptsLeft() {
        return attemptsLeft;
    }
}
