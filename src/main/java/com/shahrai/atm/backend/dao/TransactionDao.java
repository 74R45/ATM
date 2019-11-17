package com.shahrai.atm.backend.dao;

import com.shahrai.atm.backend.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository("postgresTransaction")
public class TransactionDao {

    private final JdbcTemplate jdbcTemplate;
    private final String url = "jdbc:postgresql://localhost/atm-db";
    private final String user = "postgres";
    private final String password = "password";

    //UUID id, BigDecimal sumOfMoney, Timestamp dateAndTime, String cardFrom, String cardTo
    //id uuid, sum decimal, date_time timestamp, card_number_from varchar(16), card_number_to varchar(16)
    //id, sum, date_time, card_number_from, card_number_to

    @Autowired
    public TransactionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public int insertTransaction(Transaction transaction) {
        String query = "INSERT INTO transaction(id, sum, date_time, card_number_from, card_number_to) VALUES(?,?,?,?,?)";
        int res = 0;
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setObject(1, transaction.getId());
            ps.setBigDecimal(2, transaction.getAmount());
            ps.setTimestamp(3, transaction.getDateAndTime());
            ps.setString(4, transaction.getCardFrom());
            ps.setString(5, transaction.getCardTo());

            res = ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return res;
    }

    public List<Transaction> selectAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT * FROM transaction";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                transactions.add(new Transaction(rs.getObject(1, java.util.UUID.class), rs.getBigDecimal(2), rs.getTimestamp(3),
                        rs.getString(4), rs.getString(5)));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return transactions;
    }

    public Optional<Transaction> selectTransactionById(UUID id) {
        String query = "SELECT * FROM transaction WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setObject(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(new Transaction(rs.getObject(1, java.util.UUID.class), rs.getBigDecimal(2), rs.getTimestamp(3),
                        rs.getString(4), rs.getString(5)));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return Optional.empty();
    }

    public int deleteTransactionById(UUID id) {
        String query = "DELETE FROM person WHERE itn = ?";
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

    public int updateTransactionById(UUID id, Transaction transaction) {
        String query = "UPDATE transaction " +
                "SET id = ?, sum = ?, date_time = ?, card_number_from = ?, card_number_to = ?" +
                "WHERE id = ?";
        int res = 0;
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setObject(5, id);

            ps.setBigDecimal(1, transaction.getAmount());
            ps.setTimestamp(2, transaction.getDateAndTime());
            ps.setString(3, transaction.getCardFrom());
            ps.setString(4, transaction.getCardTo());

            res = ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return res;
    }

    public List<Transaction> selectTransactionsOnPeriod(String number, Timestamp start, Timestamp end) {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT * FROM transaction WHERE (card_number_from = ? OR card_number_to = ?) AND date_time between ? and ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, number);
            ps.setString(2, number);
            ps.setTimestamp(3, start);
            ps.setTimestamp(4, end);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                transactions.add(new Transaction(rs.getObject(1, java.util.UUID.class), rs.getBigDecimal(2), rs.getTimestamp(3),
                        rs.getString(4), rs.getString(5)));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return transactions;
    }
}
