package com.shahrai.atm.backend.model;

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
    private final BigDecimal amount;
    private final BigDecimal amountCredit;
    private final BigDecimal creditLimit;
    @NotBlank
    private final String pin;

    // String number,int itn, Timestamp expiration, boolean isCredit, BigDecimal amount,BigDecimal amountCredit, String pin

    public Account(@JsonProperty("number") String number,
                   @JsonProperty("itn") String itn,
                   @JsonProperty("expiration") Timestamp expiration,
                   @JsonProperty("isCredit") boolean isCredit,
                   @JsonProperty("isBlocked") boolean isBlocked,
                   @JsonProperty("amount") BigDecimal amount,
                   @JsonProperty("amountCredit") BigDecimal amountCredit,
                   @JsonProperty("creditLimit") BigDecimal creditLimit,
                   @JsonProperty("pin") String pin) {
        this.number = number;
        this.itn = itn;
        this.expiration = expiration;
        this.isCredit = isCredit;
        this.isBlocked = isBlocked;
        this.amount = amount;
        this.amountCredit = amountCredit;
        this.creditLimit = creditLimit;
        this.pin = pin;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getAmountCredit() {
        return amountCredit;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public String getPin() {
        return pin;
    }
}