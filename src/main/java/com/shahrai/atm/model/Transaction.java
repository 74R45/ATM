package com.shahrai.atm.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

public class Transaction {
    private final UUID id;
    private final BigDecimal amount;
    private final Timestamp dateAndTime;
    @NotBlank private final String cardFrom;
    @NotBlank private final String cardTo;

    //UUID id, BigDecimal sumOfMoney, Timestamp dateAndTime, String cardFrom, String cardTo

    public Transaction(@JsonProperty("id") UUID id,
                       @JsonProperty("amount") BigDecimal amount,
                       @JsonProperty("timestamp") Timestamp dateAndTime,
                       @JsonProperty("cardFrom") String cardFrom,
                       @JsonProperty("cardTo") String cardTo) {
        this.id = id;
        this.amount = amount;
        this.dateAndTime = dateAndTime;
        this.cardFrom = cardFrom;
        this.cardTo = cardTo;
    }

    public UUID getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Timestamp getDateAndTime() {
        return dateAndTime;
    }

    public String getCardFrom() {
        return cardFrom;
    }

    public String getCardTo() {
        return cardTo;
    }
}