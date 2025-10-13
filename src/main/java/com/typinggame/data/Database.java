package com.typinggame.data;

import java.sql.*;
import com.typinggame.data.DrillSeeder;

public class Database {
    private static final String URL = "jdbc:sqlite:typinggame.db";
    private static boolean inited = false;

    public static synchronized void init() {
        if (inited) return;

        try { Class.forName("org.sqlite.JDBC"); } catch (ClassNotFoundException ignore) {}

        try (Connection c = getConnection(); Statement st = c.createStatement()) {
            // --- Users (unchanged) ---
            st.execute("""
              CREATE TABLE IF NOT EXISTS users(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username      TEXT UNIQUE NOT NULL,
                password_hash TEXT NOT NULL
              );
            """);

            // --- Drills (original shape with body/tier retained for compat) ---
            st.execute("""
              CREATE TABLE IF NOT EXISTS drills(
                id    INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT    NOT NULL,
                body  TEXT    NOT NULL,
                tier  INTEGER NOT NULL DEFAULT 1
              );
            """);

            // --- Additive migrations (idempotent) ---
            // Custom-drill metadata (you already had these)
            try (Statement st2 = c.createStatement()) {
                st2.execute("ALTER TABLE drills ADD COLUMN is_custom INTEGER DEFAULT 0");
            } catch (SQLException ignore) {}
            try (Statement st2 = c.createStatement()) {
                st2.execute("ALTER TABLE drills ADD COLUMN created_by TEXT");
            } catch (SQLException ignore) {}
            try (Statement st2 = c.createStatement()) {
                st2.execute("ALTER TABLE drills ADD COLUMN created_at INTEGER");
            } catch (SQLException ignore) {}

            // --- NEW: Levels migration ---
            // 1) Add 'level' column if it doesn't exist
            try (Statement st2 = c.createStatement()) {
                // Keep it nullable with DEFAULT 1 for maximal compatibility in SQLite
                st2.execute("ALTER TABLE drills ADD COLUMN level INTEGER DEFAULT 1");
            } catch (SQLException ignore) {}

            // 2) Backfill level from tier where sensible
            try (Statement st2 = c.createStatement()) {
                // If 'tier' exists/has data, copy it into 'level' when level is NULL or default
                // (SQLite sets new column to DEFAULT; we still overwrite to mirror previous data)
                st2.execute("""
                    UPDATE drills
                       SET level = CASE
                           WHEN tier IS NOT NULL AND tier > 0 THEN tier
                           ELSE 1
                       END
                """);
            } catch (SQLException ignore) {}

            // 3) Helpful index for level-based queries
            try (Statement st2 = c.createStatement()) {
                st2.execute("CREATE INDEX IF NOT EXISTS idx_drills_level_id ON drills(level, id)");
            } catch (SQLException ignore) {}

            // --- Sessions (unchanged; your columns preserved) ---
            st.execute("""
              CREATE TABLE IF NOT EXISTS sessions(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id          INTEGER NOT NULL,
                drill_id         INTEGER NOT NULL,
                wpm              REAL    NOT NULL,
                accuracy         REAL    NOT NULL,
                score            REAL    NOT NULL,
                typed_chars      INTEGER NOT NULL,
                duration_seconds REAL    NOT NULL,
                started_at       TEXT    NOT NULL DEFAULT (datetime('now')),
                FOREIGN KEY(user_id)  REFERENCES users(id),
                FOREIGN KEY(drill_id) REFERENCES drills(id)
              );
            """);

            // --- User settings (unchanged) ---
            st.execute("""
              CREATE TABLE IF NOT EXISTS user_settings(
                user_id      INTEGER PRIMARY KEY,
                display_name TEXT    NOT NULL DEFAULT '',
                font_family  TEXT    NOT NULL DEFAULT 'System',
                font_size    INTEGER NOT NULL DEFAULT 24,
                theme        TEXT    NOT NULL DEFAULT 'KRILL',
                FOREIGN KEY(user_id) REFERENCES users(id)
              );
            """);

            // --- Seed demo user/settings (unchanged) ---
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT OR IGNORE INTO users(username, password_hash) VALUES(?, ?)")) {
                ps.setString(1, "demo");
                ps.setString(2, Integer.toHexString("demo123".hashCode()));
                ps.executeUpdate();
            }

            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT OR IGNORE INTO user_settings(user_id, display_name) " +
                            "SELECT id, 'Demo User' FROM users WHERE username='demo'")) {
                ps.executeUpdate();
            }

            // --- Seeder: update this class to create 10 levels × 3 drills each ---
            // Keep your existing call; just ensure DrillSeeder writes 'level' (not 'tier').
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

    public static synchronized void forceReinitForTests() {
        inited = false;
    }
}
