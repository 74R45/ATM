package com.shahrai.atm.backend.dao;

import com.shahrai.atm.backend.model.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("postgresAdmin")
public class AdminDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AdminDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    int insertAdmin(Admin admin) {
        return 1;
    }

    Optional<Admin> selectPassword(String login) {
        return Optional.empty();
    }

    int deleteAdmin(String login) {
        return 1;
    }

    int updateAdmin(String login, Admin admin) {
        return 1;
    }
}