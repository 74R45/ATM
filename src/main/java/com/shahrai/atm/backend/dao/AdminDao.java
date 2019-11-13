package com.shahrai.atm.backend.dao;

import com.shahrai.atm.backend.model.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.Optional;

@Repository("postgresAdmin")
public class AdminDao {

    private final JdbcTemplate jdbcTemplate;
    private final String url = "jdbc:postgresql://localhost/atm-db";
    private final String user = "postgres";
    private final String password = "password";

    // login varchar(30), password VARCHAR(64)
    // String login, String password

    @Autowired
    public AdminDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public String insertAdmin(Admin admin) {
        String query = "INSERT INTO administrator (login, password) VALUES(?,?)";
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

    public String selectPassword(String login) {
        String query = "SELECT password FROM administrator WHERE login = ?";
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

    public int deleteAdmin(String login) {
        String query = "DELETE FROM administrator WHERE login = ?";
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

    public int updateAdmin(String login, Admin admin) {
        String query = "UPDATE administrator SET password = ?";
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