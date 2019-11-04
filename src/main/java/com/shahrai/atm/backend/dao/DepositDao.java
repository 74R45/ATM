package com.shahrai.atm.backend.dao;

import com.shahrai.atm.backend.model.Deposit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository("postgresDeposit")
public class DepositDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DepositDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int insertDeposit(Deposit deposit) {
        return 1;
    }

    public Optional<Deposit> selectDepositById(UUID id) {
        return Optional.empty();
    }

    public int deleteDepositById(UUID id) {
        return 1;
    }

    public int updateDepositById(UUID id, Deposit deposit) {
        return 1;
    }
}
