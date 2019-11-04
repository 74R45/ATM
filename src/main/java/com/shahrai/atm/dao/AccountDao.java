package com.shahrai.atm.dao;

import com.shahrai.atm.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("postgresAccount")
public class AccountDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    int insertAccount(Account account) {
        return 1;
    }

    List<Account> selectAllAccounts() {
        return null;
    }

    Optional<Account> selectAccountByNumber(String number) {
        return Optional.empty();
    }

    int deleteAccountByNumber(String number) {
        return 1;
    }

    int updateAccountByNumber(String number, Account account) {
        return 1;
    }
}
