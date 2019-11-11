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
    private final String url = "jdbc:postgresql://localhost/dvdrental";
    private final String user = "postgres";
    private final String password = "postgres";

    // String number, int itn, Timestamp expiration, boolean isCredit, BigDecimal amount,BigDecimal amountCredit, String pin
    // card_num varchar(16), itn int, expiration date,  is_credit_card boolean, amount decimal,  amount_credit decimal, PIN int
    // card_num, itn, expiration, is_credit_card, amount, amount_credit, PIN

    @Autowired
    public AccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public String insertAccount(Account account) {
        String query = "INSERT INTO account (card_num, itn, expiration, is_credit_card, amount, amount_credit, PIN) VALUES (?,?,?,?,?,?,?)";
        String id = "";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(query,
                     Statement.RETURN_GENERATED_KEYS)) { // what is that

            ps.setString(1, account.getNumber());
            ps.setInt(2, account.getItn());
            ps.setTimestamp(3, account.getExpiration());
            ps.setBoolean(4, account.isCredit());
            ps.setBigDecimal(5, account.getAmount());
            ps.setBigDecimal(6, account.getAmountCredit());
            ps.setString(7, account.getPin());

            int affectedRows = ps.executeUpdate();
            // check the affected rows
            if (affectedRows > 0) {
                // get the ID back
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        id = rs.getString(1);
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return id;
    }

    public List<Account> selectAllAccounts() {
        List<Account> accounts = new ArrayList<Account>();
        String query = "SELECT * FROM account";
        try {
            Connection conn = connect();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String number = rs.getString(1);
                int itn = rs.getInt(2);
                Timestamp expiration = rs.getTimestamp(3); // WILL IT WORK??
                boolean isCredit = rs.getBoolean(4);
                BigDecimal amount = rs.getBigDecimal(5);
                BigDecimal amountCredit = rs.getBigDecimal(6);
                String pin = rs.getString(7);
                Account acc = new Account(number, itn, expiration, isCredit, amount, amountCredit, pin);
                accounts.add(acc);
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
            while (rs.next()) {
                String card_number = rs.getString(1);
                int itn = rs.getInt(2);
                Timestamp expiration = rs.getTimestamp(3); // WILL IT WORK??
                boolean isCredit = rs.getBoolean(4);
                BigDecimal amount = rs.getBigDecimal(5);
                BigDecimal amountCredit = rs.getBigDecimal(6);
                String pin = rs.getString(7);
                Account acc = new Account(card_number, itn, expiration, isCredit, amount, amountCredit, pin);
                Optional<Account> accountOptional = Optional.of(acc);
                return accountOptional;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return Optional.empty();
    }

    // index and Optional??
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
                "is_credit_card = ?, amount = ?, amount_credit = ?, PIN = ?" +
                "WHERE card_num = ?";
        int res = 0;
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(7, number);

            ps.setInt(1, account.getItn());
            ps.setTimestamp(2, account.getExpiration());
            ps.setBoolean(3, account.isCredit());
            ps.setBigDecimal(4, account.getAmount());
            ps.setBigDecimal(5, account.getAmountCredit());
            ps.setString(6, account.getPin());

            res = ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return res;
    }
}
