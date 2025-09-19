package com.typinggame.data;

import org.junit.jupiter.api.*;
import java.nio.file.*;
import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {

    private static final Path DB = Paths.get("typinggame.db");

    @BeforeEach
    void resetDb() throws Exception {
        Files.deleteIfExists(Paths.get("typinggame.db"));
        Database.forceReinitForTests();   // << reset the guard
        Database.init();                  // recreate schema + seed
    }

    @Test
    void init_createsSchema_andSeedsDemo() throws Exception {
        Database.init();

        try (Connection c = Database.getConnection()) {
            // tables exist
            assertTrue(tableExists(c, "users"));
            assertTrue(tableExists(c, "drills"));
            assertTrue(tableExists(c, "sessions"));
            assertTrue(tableExists(c, "user_settings"));

            // demo user exists
            Integer demoId = userId(c, "demo");
            assertNotNull(demoId);

            // settings for demo exist
            assertEquals(1, count(c, "user_settings WHERE user_id=" + demoId));

            // at least one drill exists
            assertTrue(count(c, "drills") > 0);
        }
    }

    @Test
    void init_isIdempotent() throws Exception {
        Database.init();
        int users1, drills1;
        try (Connection c = Database.getConnection()) {
            users1 = count(c, "users");
            drills1 = count(c, "drills");
        }
        Database.init();
        try (Connection c = Database.getConnection()) {
            assertEquals(users1, count(c, "users"));
            assertEquals(drills1, count(c, "drills"));
        }
    }

    @Test
    void canInsertSession_forDemoAndSeededDrill() throws Exception {
        Database.init();

        try (Connection c = Database.getConnection()) {
            Integer demoId = userId(c, "demo");
            assertNotNull(demoId);
            Integer drillId = anyDrillId(c);
            assertNotNull(drillId);

            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO sessions(user_id, drill_id, wpm, accuracy, score, typed_chars, duration_seconds) " +
                            "VALUES(?,?,?,?,?,?,?)")) {
                ps.setInt(1, demoId);
                ps.setInt(2, drillId);
                ps.setDouble(3, 70.0);
                ps.setDouble(4, 95.0);       // accuracy in percent
                ps.setDouble(5, 70.0 * 95.0);
                ps.setInt(6, 200);
                ps.setDouble(7, 60.0);
                assertEquals(1, ps.executeUpdate());
            }

            assertEquals(1, count(c, "sessions"));
        }
    }

    // ---- helpers ----
    private static boolean tableExists(Connection c, String name) throws SQLException {
        try (ResultSet rs = c.getMetaData().getTables(null, null, name, null)) {
            return rs.next();
        }
    }
    private static int count(Connection c, String tableOrWhere) throws SQLException {
        try (Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM " + tableOrWhere)) {
            rs.next(); return rs.getInt(1);
        }
    }
    private static Integer userId(Connection c, String username) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT id FROM users WHERE username=?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getInt(1) : null; }
        }
    }
    private static Integer anyDrillId(Connection c) throws SQLException {
        try (Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT id FROM drills LIMIT 1")) {
            return rs.next() ? rs.getInt(1) : null;
        }
    }
}
