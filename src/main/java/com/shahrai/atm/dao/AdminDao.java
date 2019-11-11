package com.shahrai.atm.dao;

import com.shahrai.atm.model.Account;
import com.shahrai.atm.model.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Optional;

@Repository("postgresAdmin")
public class AdminDao {

    private final JdbcTemplate jdbcTemplate;
    private final String url = "jdbc:postgresql://localhost/dvdrental";
    private final String user = "postgres";
    private final String password = "postgres";

    // login varchar(30), password VARCHAR(50)
    // String login, String password

    @Autowired
    public AdminDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    String insertAdmin(Admin admin) {
        String query = "INSERT INTO Administrator(login, password) VALUES(?,?)";
        String id = "";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(query,
                     Statement.RETURN_GENERATED_KEYS)) { // what is that

            ps.setString(1, admin.getLogin());
            ps.setString(2, admin.getPassword());

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

    Optional<Admin> selectPassword(String login) {
        String query = "SELECT password FROM Administrator WHERE login = ?";
        String password = "";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                password = rs.getString(1);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return password;
    }

    int deleteAdmin(String login) {
        String query = "DELETE FROM Administrator WHERE login = ?";
        int res = 0;
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, login);
            res = ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return res;
    }

    int updateAdmin(String login, Admin admin) {
        String query = "UPDATE Administrator SET password = ?";
        int res = 0;
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, admin.getPassword());
            res = ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return res;
    }
}