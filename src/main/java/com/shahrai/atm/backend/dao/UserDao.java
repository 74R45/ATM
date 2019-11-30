package com.shahrai.atm.backend.dao;

import com.shahrai.atm.backend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("postgresUser")
public class UserDao {
    private final JdbcTemplate jdbcTemplate;
    private final String url = "jdbc:postgresql://localhost/atm-db";
    private final String user = "postgres";
    private final String password = "password";

    // int itn, String name, String surname, String patronymic, String login, String password, String question, String answer
    // itn int, first_name varchar(50), surname varchar(50), patronymic varchar(50), login varchar(30),password varchar(30), control_question varchar(200),answer_on_cq varchar(50)
    // itn, first_name, surname, patronymic, login, password, control_question, answer_on_cq

    @Autowired
    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public int insertUser(User user) {
        String query = "INSERT INTO person(itn, first_name, surname, patronymic, login, password, control_question, answer_on_cq) " +
                "VALUES(?,?,?,?,?,?,?,?)";
        int res = 0;
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, user.getItn());
            ps.setString(2, user.getName());
            ps.setString(3, user.getSurname());
            ps.setString(4, user.getPatronymic());
            ps.setString(5, user.getLogin());
            ps.setString(6, user.getPassword());
            ps.setString(7, user.getQuestion());
            ps.setString(7, user.getAnswer());

            res = ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return res;
    }

    public List<User> selectAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM person";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(new User(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                        rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(7)));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return users;
    }

    public Optional<User> selectUserByItn(String itn) {
        String query = "SELECT * FROM person WHERE itn = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, itn);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(new User(itn, rs.getString(2), rs.getString(3), rs.getString(4),
                        rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(7)));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return Optional.empty();
    }

    public int deleteUserByItn(String itn) {
        String query = "DELETE FROM person WHERE itn = ?";
        int res = 0;
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, itn);
            res = ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return res;
    }

    public int updateUserByItn(String itn, User user) {
        String query = "UPDATE person " +
                "SET first_name = ?, surname = ?, patronymic = ?," +
                "login = ?, password = ?, control_question = ?, answer_on_cq = ?" +
                "WHERE itn = ?";
        int res = 0;
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(8, itn);

            ps.setString(1, user.getName());
            ps.setString(2, user.getSurname());
            ps.setString(3, user.getPatronymic());
            ps.setString(4, user.getLogin());
            ps.setString(5, user.getPassword());
            ps.setString(6, user.getQuestion());
            ps.setString(7, user.getAnswer());

            res = ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return res;
    }

    public Optional<User> selectUserByLogin(String login) {
        String query = "SELECT * FROM person WHERE login = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(new User(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                        rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8)));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return Optional.empty();
    }
}
