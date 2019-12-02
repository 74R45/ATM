package com.shahrai.atm.dao;

import com.shahrai.atm.model.Deposit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository("postgresDeposit")
public class DepositDao {

    private final JdbcTemplate jdbcTemplate;
    private final String url = "jdbc:postgresql://localhost/atm-db";
    private final String user = "postgres";
    private final String password = "password";

    // UUID idб, String itn, Timestamp expiration, BigDecimal deposited, BigDecimal accrued
    // id uuid, itn varchar(12), expiration_ timestamp, deposited_money decimal, accrued_money decimal
    // id, itn, expiration_ , deposited_money, accrued_money

    @Autowired
    public DepositDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public int insertDeposit(Deposit deposit) {
        String query = "INSERT INTO deposit(id, itn, expiration, deposited_money) VALUES(?,?,?,?)";
        int res = 0;
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setObject(1, deposit.getId());
            ps.setString(2, deposit.getItn());
            ps.setTimestamp(3, deposit.getExpiration());
            ps.setBigDecimal(4, deposit.getAmount());

            res = ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return res;
    }

    public List<Deposit> selectAllAccounts() {
        List<Deposit> accounts = new ArrayList<>();
        String query = "SELECT * FROM deposit";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                accounts.add(new Deposit(rs.getObject(1, java.util.UUID.class), rs.getString(2), rs.getTimestamp(3),
                        rs.getBigDecimal(4)));
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return accounts;
    }

    public Optional<Deposit> selectDepositById(UUID id) {
        String query = "SELECT * FROM deposit WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setObject(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(new Deposit(id, rs.getString(2), rs.getTimestamp(3),
                        rs.getBigDecimal(4)));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return Optional.empty();
    }

    public int deleteDepositById(UUID id) {
        String query = "DELETE FROM deposit WHERE id = ?";
        int res = 0;
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setObject(1, id);
            res = ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return res;
    }

    public int updateDepositById(UUID id, Deposit deposit) {
        String query = "UPDATE deposit " +
                "SET itn = ?, expiration = ?, deposited_money = ?" +
                "WHERE id = ?";
        int res = 0;
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setObject(4, id);

            ps.setString(1, deposit.getItn());
            ps.setTimestamp(2, deposit.getExpiration());
            ps.setBigDecimal(3, deposit.getAmount());

            res = ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return res;
    }

    public List<Deposit> selectDepositsByItn(String itn) {
        List<Deposit> deposits = new ArrayList<>();
        String query = "SELECT * FROM deposit WHERE itn = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, itn);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                deposits.add(new Deposit(rs.getObject(1, java.util.UUID.class), rs.getString(2), rs.getTimestamp(3),
                        rs.getBigDecimal(4)));
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return deposits;
    }
}