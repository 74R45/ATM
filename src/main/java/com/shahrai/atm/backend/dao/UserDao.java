package com.shahrai.atm.backend.dao;

import com.shahrai.atm.backend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("postgresUser")
public class UserDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int insertUser(User user) {
        return 1;
    }

    public List<User> selectAllUsers() {
        return null;
    }

    public Optional<User> selectPersonByItn(int itn) {
        return Optional.empty();
    }

    public int deletePUserByItn(int itn) {
        return 1;
    }

    public int updateUserByItn(int itn, User user) {
        return 1;
    }
}