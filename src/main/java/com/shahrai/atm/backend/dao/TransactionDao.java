package com.shahrai.atm.backend.dao;

import com.shahrai.atm.backend.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
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

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

//    public int insertTransaction(Transaction transaction) {
//        String query = "INSERT INTO transaction(id, sum, date_time, card_number_from, card_number_to) VALUES(?,?,?,?,?)";
//        String id = "";
//        try (Connection conn = connect();
//             PreparedStatement ps = conn.prepareStatement(query,
//                     Statement.RETURN_GENERATED_KEYS)) { // what is that
//
//            //UUID id, BigDecimal sumOfMoney, Timestamp dateAndTime, String cardFrom, String cardTo
//            ps.setInt(1, account.getNumber());
//            ps.setInt(2, account.getItn());
//            ps.setDate(3, account.getExpiration());
//            ps.setBoolean(4, account.isCredit());
//            ps.setBigDecimal(5, account.getAmount());
//            ps.setBigDecimal(6, account.getAmountCredit());
//            ps.setString(7, account.getPin());
//
//            int affectedRows = ps.executeUpdate();
//            // check the affected rows
//            if (affectedRows > 0) {
//                // get the ID back
//                try (ResultSet rs = ps.getGeneratedKeys()) {
//                    if (rs.next()) {
//                        id = rs.getString(1);
//                    }
//                } catch (SQLException ex) {
//                    System.out.println(ex.getMessage());
//                }
//            }
//        } catch (SQLException ex) {
//            System.out.println(ex.getMessage());
//        }
//        return id;
//    }

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
