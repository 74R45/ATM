package com.shahrai.atm.dao;

import com.shahrai.atm.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository("postgresTransaction")
public class TransactionDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TransactionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int insertTransaction(Transaction transaction) {
        return 1;
    }

    public List<Transaction> selectAllTransactions() {
        return null;
    }

    public Optional<Transaction> selectTransactionById(UUID id) {
        return Optional.empty();
    }

    public int deleteTransactionById(UUID id) {
        return 1;
    }

    public int updateTransactionById(UUID id, Transaction transaction) {
        return 1;
    }
}
