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
    private final String url = "jdbc:postgresql://localhost/dvdrental";
    private final String user = "postgres";
    private final String password = "postgres";

    // int itn, String name, String surname, String patronymic, String login, String password, String question, String answer
    // itn int, first_name varchar(50), surname varchar(50), patronymic varchar(50), login varchar(30),password varchar(30), control_question varchar(200),answer_on_cq varchar(50)
    // itn, first_name, surname, patronymic, login, password, control_question, answer_on_cq

    @Autowired
    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public int insertUser(User user) {
        String query = "INSERT INTO User(itn, first_name, surname, patronymic, login, password, control_question, answer_on_cq) " +
                "VALUES(?,?,?,?,?,?,?,?)";
        int id = 0;
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(query,
                     Statement.RETURN_GENERATED_KEYS)) { // what is that

            ps.setInt(1, user.getItn());
            ps.setString(2, user.getName());
            ps.setString(3, user.getSurname());
            ps.setString(4, user.getPatronymic());
            ps.setString(5, user.getLogin());
            ps.setString(6, user.getPassword());
            ps.setString(7, user.getQuestion());
            ps.setString(7, user.getAnswer());

            int affectedRows = ps.executeUpdate();
            // check the affected rows
            if (affectedRows > 0) {
                // get the ID back
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        id = rs.getInt(1);
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

    public List<User> selectAllUsers() {
        List<User> users = new ArrayList<User>();
        String query = "SELECT * FROM User";
        try {
            Connection conn = connect();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int itn = rs.getInt(1);
                String name = rs.getString(2);
                String surname = rs.getString(3);
                String patronymic = rs.getString(4);
                String login = rs.getString(5);
                String password = rs.getString(6);
                String cq = rs.getString(7);
                String answer_cq = rs.getString(7);
                User user = new User(itn, name, surname, patronymic, login, password, cq, answer_cq);
                users.add(user);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return users;
    }

    public Optional<User> selectPersonByItn(int itn) {
        String query = "SELECT * FROM User WHERE itn = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, itn);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int itn_db = rs.getInt(1);
                String name = rs.getString(2);
                String surname = rs.getString(3);
                String patronymic = rs.getString(4);
                String login = rs.getString(5);
                String password = rs.getString(6);
                String cq = rs.getString(7);
                String answer_cq = rs.getString(7);
                return Optional.of(new User(itn_db, name, surname, patronymic, login, password, cq, answer_cq));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return Optional.empty();
    }

    public int deletePUserByItn(int itn) {
        String query = "DELETE FROM User WHERE itn = ?";
        int res = 0;
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, itn);
            res = ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return res;
    }

    public int updateUserByItn(int itn, User user) {
        String query = "UPDATE User " +
                "SET itn = ?, first_name = ?, surname = ?, patronymic = ?," +
                "login = ?, password = ?, control_question = ?, answer_on_cq = ?" +
                "WHERE card_num = ?";
        int res = 0;
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(8, itn);

            ps.setString(1, user.getName());
            ps.setString(2, user.getSurname()); // ?????
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
}
