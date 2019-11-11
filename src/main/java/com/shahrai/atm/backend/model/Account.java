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
    private final BigDecimal amount;
    private final BigDecimal amountCredit;
    @NotBlank
    private final String pin;

    // String number,int itn, Timestamp expiration, boolean isCredit, BigDecimal amount,BigDecimal amountCredit, String pin

    public Account(@JsonProperty("number") String number,
                   @JsonProperty("itn") String itn,
                   @JsonProperty("expiration") Timestamp expiration,
                   @JsonProperty("isCredit") boolean isCredit,
                   @JsonProperty("amount") BigDecimal amount,
                   @JsonProperty("amountCredit") BigDecimal amountCredit,
                   @JsonProperty("pin") String pin) {
        this.number = number;
        this.itn = itn;
        this.expiration = expiration;
        this.isCredit = isCredit;
        this.amount = amount;
        this.amountCredit = amountCredit;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getAmountCredit() {
        return amountCredit;
    }

    public String getPin() {
        return pin;
    }
}