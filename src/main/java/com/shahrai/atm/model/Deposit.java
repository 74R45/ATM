package com.shahrai.atm.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

public class Deposit {
    private final UUID id;
    @NotBlank private final String itn;
    private final Timestamp expiration;
    private final BigDecimal deposited;
    private final BigDecimal accrued;

    public Deposit(@JsonProperty("id") UUID id,
                   @JsonProperty("itn") String itn,
                   @JsonProperty("expiration") Timestamp expiration,
                   @JsonProperty("deposited") BigDecimal deposited,
                   @JsonProperty("accrued") BigDecimal accrued) {
        this.id = id;
        this.itn = itn;
        this.expiration = expiration;
        this.deposited = deposited;
        this.accrued = accrued;
    }

    public UUID getId() {
        return id;
    }

    public String getItn() {
        return itn;
    }

    public Timestamp getExpiration() {
        return expiration;
    }

    public BigDecimal getDeposited() {
        return deposited;
    }

    public BigDecimal getAccrued() {
        return accrued;
    }
}
