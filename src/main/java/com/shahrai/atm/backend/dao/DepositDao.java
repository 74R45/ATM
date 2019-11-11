package com.shahrai.atm.backend.dao;

import com.shahrai.atm.backend.model.Deposit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@Repository("postgresDeposit")
public class DepositDao {

    private final JdbcTemplate jdbcTemplate;
    private final String url = "jdbc:postgresql://localhost/dvdrental";
    private final String user = "postgres";
    private final String password = "postgres";

    // UUID idб, String itn, Timestamp expiration, BigDecimal deposited, BigDecimal accrued
    // id uuid, itn integer, expiration_ date, deposited_money integer, accrued_money integer

    @Autowired
    public DepositDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
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
