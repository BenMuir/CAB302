package com.typinggame.data;

import java.sql.*;
import com.typinggame.data.DrillSeeder;

public class Database {
    private static final String URL = "jdbc:sqlite:typinggame.db";
    private static boolean inited = false;

    public static synchronized void init() {
        if (inited) return;

        // Optional: load driver explicitly for clearer errors
        try { Class.forName("org.sqlite.JDBC"); } catch (ClassNotFoundException ignore) {}

        try (Connection c = getConnection(); Statement st = c.createStatement()) {
            // Users with hashed password
            st.execute("""
              CREATE TABLE IF NOT EXISTS users(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username      TEXT UNIQUE NOT NULL,
                password_hash TEXT NOT NULL
              );
            """);

            // Drills (basic seed so scores can reference drill 1)
            st.execute("""
              CREATE TABLE IF NOT EXISTS drills(
                id    INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT    NOT NULL,
                body  TEXT    NOT NULL,
                tier  INTEGER NOT NULL DEFAULT 1
              );
            """);

            // Scores (store accuracy as percentage for simplicity)
            st.execute("""
              CREATE TABLE IF NOT EXISTS sessions(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id          INTEGER NOT NULL,
                drill_id         INTEGER NOT NULL,
                wpm              REAL    NOT NULL,
                accuracy         REAL    NOT NULL,   -- 0..100
                score            REAL    NOT NULL,   -- WPM * Accuracy
                typed_chars      INTEGER NOT NULL,
                duration_seconds REAL    NOT NULL,
                started_at       TEXT    NOT NULL DEFAULT (datetime('now')),
                FOREIGN KEY(user_id)  REFERENCES users(id),
                FOREIGN KEY(drill_id) REFERENCES drills(id)
              );
            """);

            // User settings / preferences (per-user row)
            st.execute("""
              CREATE TABLE IF NOT EXISTS user_settings(
                user_id      INTEGER PRIMARY KEY,
                display_name TEXT    NOT NULL DEFAULT '',
                font_family  TEXT    NOT NULL DEFAULT 'System',
                font_size    INTEGER NOT NULL DEFAULT 16,
                theme        TEXT    NOT NULL DEFAULT 'Light',
                FOREIGN KEY(user_id) REFERENCES users(id)
              );
            """);

            // Seed demo user (hash matches UserManager.hash())
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT OR IGNORE INTO users(username, password_hash) VALUES(?, ?)")) {
                ps.setString(1, "demo");
                ps.setString(2, Integer.toHexString("demo123".hashCode()));
                ps.executeUpdate();
            }

            // Ensure demo has default settings
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT OR IGNORE INTO user_settings(user_id, display_name) " +
                            "SELECT id, 'Demo User' FROM users WHERE username='demo'")) {
                ps.executeUpdate();
            }

            // Ensure baseline drills exist (and add tier column if needed)
            DrillSeeder.ensureBaselineDrills();

            inited = true;
            System.out.println("DB init OK -> typinggame.db");
        } catch (SQLException e) {
            throw new RuntimeException("DB init failed: " + e.getMessage(), e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}