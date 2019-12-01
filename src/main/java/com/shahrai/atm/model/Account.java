package com.shahrai.atm.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class Account {
    @NotBlank
    private final String number;
    private final String itn;
    private final Timestamp expiration;
    private final boolean isCredit;
    private final boolean isBlocked;
    private final Timestamp deletionTime;
    private final BigDecimal amount;
    private final BigDecimal amountCredit;
    private final BigDecimal creditLimit;
    private final Timestamp nextCreditTime;
    @NotBlank
    private final String pin;
    private final int attemptsLeft;

    // String number,int itn, Timestamp expiration, boolean isCredit, BigDecimal amount,BigDecimal amountCredit, String pin

    public Account(@JsonProperty("number") String number,
                   @JsonProperty("itn") String itn,
                   @JsonProperty("expiration") Timestamp expiration,
                   @JsonProperty("isCredit") boolean isCredit,
                   @JsonProperty("isBlocked") boolean isBlocked,
                   @JsonProperty("deletionTime") Timestamp deletionTime,
                   @JsonProperty("amount") BigDecimal amount,
                   @JsonProperty("amountCredit") BigDecimal amountCredit,
                   @JsonProperty("creditLimit") BigDecimal creditLimit,
                   @JsonProperty("nextCreditTime") Timestamp nextCreditTime,
                   @JsonProperty("pin") String pin,
                   @JsonProperty("attemptsLeft") int attemptsLeft) {
        this.number = number;
        this.itn = itn;
        this.expiration = expiration;
        this.isCredit = isCredit;
        this.isBlocked = isBlocked;
        this.deletionTime = deletionTime;
        this.amount = amount;
        this.amountCredit = amountCredit;
        this.creditLimit = creditLimit;
        this.nextCreditTime = nextCreditTime;
        this.pin = pin;
        this.attemptsLeft = attemptsLeft;
    }

    public String getNumber() {
        return number;
    }

    public String getItn() {
        return itn;
    }

    public Timestamp getExpiration() {
        return expiration;
    }

    public boolean isCredit() {
        return isCredit;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public Timestamp getDeletionTime() {
        return deletionTime;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getAmountCredit() {
        return amountCredit;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public Timestamp getNextCreditTime() {
        return nextCreditTime;
    }

    public String getPin() {
        return pin;
    }

    public int getAttemptsLeft() {
        return attemptsLeft;
    }
}