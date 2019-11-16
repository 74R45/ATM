package com.shahrai.atm.backend.dao;

import com.shahrai.atm.backend.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("postgresAccount")
public class AccountDao {

    private final JdbcTemplate jdbcTemplate;
    private final String url = "jdbc:postgresql://localhost/atm-db";
    private final String user = "postgres";
    private final String password = "password";

    // String number, int itn, Timestamp expiration, boolean isCredit, BigDecimal amount,BigDecimal amountCredit, String pin
    // card_num varchar(16), itn int, expiration date,  is_credit_card boolean, amount decimal,  amount_credit decimal, PIN int
    // card_num, itn, expiration, is_credit_card, amount, amount_credit, PIN

    @Autowired
    public AccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public int insertAccount(Account account) {
        String query = "INSERT INTO account(card_num, itn, expiration, is_credit_card, is_blocked, amount, amount_credit, credit_limit, PIN) VALUES(?,?,?,?,?,?,?,?,?)";
        int res = 0;
        try (Connection conn = connect();
            PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, account.getNumber());
            ps.setString(2, account.getItn());
            ps.setTimestamp(3, account.getExpiration());
            ps.setBoolean(4, account.isCredit());
            ps.setBoolean(5, account.isBlocked());
            ps.setBigDecimal(6, account.getAmount());
            ps.setBigDecimal(7, account.getAmountCredit());
            ps.setBigDecimal(8, account.getCreditLimit());
            ps.setString(9, account.getPin());

            res = ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return res;
    }

    public List<Account> selectAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        String query = "SELECT * FROM account";
        try (Connection conn = connect();
            PreparedStatement ps = conn.prepareStatement(query)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                accounts.add(new Account(rs.getString(1), rs.getString(2), rs.getTimestamp(3),
                        rs.getBoolean(4), rs.getBoolean(5), rs.getBigDecimal(6),
                        rs.getBigDecimal(7), rs.getBigDecimal(8), rs.getString(9)));
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return accounts;
    }

    public Optional<Account> selectAccountByNumber(String number) {
        String query = "SELECT * FROM account WHERE card_num = ?";
        try (Connection conn = connect();
            PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, number);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(new Account(rs.getString(1), rs.getString(2), rs.getTimestamp(3),
                        rs.getBoolean(4), rs.getBoolean(5), rs.getBigDecimal(6),
                        rs.getBigDecimal(7), rs.getBigDecimal(8), rs.getString(9)));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return Optional.empty();
    }

    public int deleteAccountByNumber(String number) {
        String query = "DELETE FROM account WHERE card_num = ?";
        int res = 0;
        try (Connection conn = connect();
            PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, number);
            res = ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return res;
    }

    public int updateAccountByNumber(String number, Account account) {
        String query = "UPDATE account " +
                "SET itn = ?, expiration = ?, " +
                "is_credit_card = ?, is_blocked = ?, amount = ?, amount_credit = ?, credit_limit = ?, PIN = ?" +
                "WHERE card_num = ?";
        int res = 0;
        try (Connection conn = connect();
            PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(9, number);

            ps.setString(1, account.getItn());
            ps.setTimestamp(2, account.getExpiration());
            ps.setBoolean(3, account.isCredit());
            ps.setBoolean(4, account.isBlocked());
            ps.setBigDecimal(5, account.getAmount());
            ps.setBigDecimal(6, account.getAmountCredit());
            ps.setBigDecimal(7, account.getCreditLimit());
            ps.setString(8, account.getPin());

            res = ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return res;
    }

    public List<Account> selectAccountsByItn(String itn) {
        List<Account> accounts = new ArrayList<>();
        String query = "SELECT * FROM account WHERE itn = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, itn);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                accounts.add(new Account(rs.getString(1), rs.getString(2), rs.getTimestamp(3),
                        rs.getBoolean(4), rs.getBoolean(5), rs.getBigDecimal(6),
                        rs.getBigDecimal(7), rs.getBigDecimal(8), rs.getString(9)));
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return accounts;
    }
}