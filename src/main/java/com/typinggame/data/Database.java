package com.typinggame.data;

import java.sql.*;

/**
 * Central SQLite bootstrap + connection helper.
 * - Creates/updates tables on first load
 * - Seeds 6 demo drills if empty
 *
 * This class is safe to load multiple times; all DDL is idempotent.
 */
public final class Database {

    private static final String DB_URL = "jdbc:sqlite:typinggame.db";

    static {
        try {
            // Ensure driver is present, then create/upgrade schema and seed data
            Class.forName("org.sqlite.JDBC");
            createTables();
            ensureDrillsTableHasTier();
            seedDrillsIfEmpty();
        } catch (Exception e) {
            throw new RuntimeException("Database init failed", e);
        }
    }

    private Database() {}

    /** Touch this once at app start if you want to force class initialization. */
    public static void init() { /* no-op */ }

    /** Opens a new connection with foreign keys enabled. Caller should close(). */
    public static Connection getConnection() throws SQLException {
        Connection c = DriverManager.getConnection(DB_URL);
        try (Statement st = c.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON;");
        }
        return c;
    }

    // ========================= Schema =========================

    private static void createTables() {
        try (Connection c = getConnection();
             Statement st = c.createStatement()) {

            // Users table (keep columns generic; your sign-up flow fills it)
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS users (
                  id            INTEGER PRIMARY KEY AUTOINCREMENT,
                  username      TEXT    NOT NULL UNIQUE,
                  password_hash TEXT    NOT NULL,
                  display_name  TEXT    NOT NULL
                );
            """);

            // Drills table (tier is ensured in ensureDrillsTableHasTier)
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS drills (
                  id    INTEGER PRIMARY KEY,
                  title TEXT    NOT NULL,
                  body  TEXT    NOT NULL,
                  tier  INTEGER NOT NULL
                );
            """);

            // Sessions table
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS sessions (
                  id               INTEGER PRIMARY KEY AUTOINCREMENT,
                  user_id          INTEGER NOT NULL,
                  drill_id         INTEGER NOT NULL,
                  wpm              REAL    NOT NULL,
                  accuracy         REAL    NOT NULL,   -- 0..100
                  score            REAL    NOT NULL,   -- usually wpm * accuracy
                  typed_chars      INTEGER NOT NULL,
                  duration_seconds REAL    NOT NULL,
                  started_at       TEXT    NOT NULL,   -- ISO-8601 instant
                  FOREIGN KEY(user_id) REFERENCES users(id)   ON DELETE CASCADE,
                  FOREIGN KEY(drill_id) REFERENCES drills(id) ON DELETE CASCADE
                );
            """);

        } catch (SQLException e) {
            throw new RuntimeException("createTables failed", e);
        }
    }

    /**
     * Migrates older DBs that had drills without a `tier` column.
     * Adds the column with default 1 if missing.
     */
    private static void ensureDrillsTableHasTier() {
        try (Connection c = getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("PRAGMA table_info('drills')")) {

            boolean hasTier = false;
            while (rs.next()) {
                if ("tier".equalsIgnoreCase(rs.getString("name"))) {
                    hasTier = true;
                    break;
                }
            }
            if (!hasTier) {
                st.executeUpdate("ALTER TABLE drills ADD COLUMN tier INTEGER NOT NULL DEFAULT 1;");
            }
        } catch (SQLException e) {
            throw new RuntimeException("ensureDrillsTableHasTier failed", e);
        }
    }

    // ========================= Seed data =========================

    private static void seedDrillsIfEmpty() {
        try (Connection c = getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) AS c FROM drills")) {

            int count = rs.next() ? rs.getInt("c") : 0;
            if (count > 0) return;

            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO drills(id, title, body, tier) VALUES(?,?,?,?)")) {
                insertDrill(ps, 1, "Easy 1",   "cat dog sun run fun", 1);
                insertDrill(ps, 2, "Easy 2",   "time day night light bright", 1);
                insertDrill(ps, 3, "Easy 3",   "red blue green yellow orange", 1);
                insertDrill(ps, 4, "Medium 1", "The quick brown fox jumps over the lazy dog.", 2);
                insertDrill(ps, 5, "Medium 2", "Typing fast is fun, but accuracy is even better!", 2);
                insertDrill(ps, 6, "Hard 1",   "Complexity arises when we type: symbols, commas, and quotes—yet fluency must remain.", 3);
            }
            System.out.println("Seeded drills table (6 rows).");

        } catch (SQLException e) {
            throw new RuntimeException("seedDrillsIfEmpty failed", e);
        }
    }

    private static void insertDrill(PreparedStatement ps, int id, String title, String body, int tier) throws SQLException {
        ps.setInt(1, id);
        ps.setString(2, title);
        ps.setString(3, body);
        ps.setInt(4, tier);
        ps.addBatch();
        ps.executeBatch();
    }

    // ========================= Optional helpers =========================
    // If you later want to seed a dev user, add a method here, but avoid referencing
    // a password hasher class unless you know its exact name/signature to prevent compile errors.
}
