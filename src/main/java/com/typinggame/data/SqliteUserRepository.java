package com.typinggame.data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqliteUserRepository implements UserRepository {

    public SqliteUserRepository() {
        Database.init();
    }

    @Override
    public boolean saveUser(User user) {
        try (Connection c = Database.getConnection()) {
            // update first
            try (PreparedStatement upd = c.prepareStatement(
                    "UPDATE users SET password_hash=? WHERE username=?")) {
                upd.setString(1, user.getPasswordHash());
                upd.setString(2, user.getUsername());
                if (upd.executeUpdate() > 0) return true;
            }
            // else insert
            try (PreparedStatement ins = c.prepareStatement(
                    "INSERT INTO users(username, password_hash) VALUES(?, ?)")) {
                ins.setString(1, user.getUsername());
                ins.setString(2, user.getPasswordHash());
                return ins.executeUpdate() == 1;
            }
        } catch (SQLException e) {
            System.err.println("saveUser failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public User loadUser(String username) {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT username, password_hash FROM users WHERE username = ? COLLATE NOCASE")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new User(rs.getString("username"), rs.getString("password_hash"));
            }
        } catch (SQLException e) {
            System.err.println("loadUser failed: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean userExists(String username) {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT 1 FROM users WHERE username = ? COLLATE NOCASE")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) {
            System.err.println("userExists failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> out = new ArrayList<>();
        try (Connection c = Database.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT username, password_hash FROM users ORDER BY username")) {
            while (rs.next()) out.add(new User(rs.getString(1), rs.getString(2)));
        } catch (SQLException e) {
            System.err.println("getAllUsers failed: " + e.getMessage());
        }
        return out;
    }
}
