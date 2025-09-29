package com.typinggame.data;

import java.sql.*;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Seeds baseline drills if missing. Idempotent (UPSERT).
 */
public final class DrillSeeder {
    private DrillSeeder() {}

    /** Ensure baseline drills exist (adds/updates rows). */
    public static void ensureBaselineDrills() {
        try (Connection c = Database.getConnection()) {
            // Find column names that exist in the current schema.
            String bodyCol = findExistingColumn(c, "drills",
                    new String[]{"body", "sentence", "text", "content"});
            String tierCol = findExistingColumnOrDefault(c, "drills",
                    new String[]{"tier", "difficulty", "difficulty_tier", "difficultyTier"}, "tier");

            // Add tier column if missing and we can call it "tier".
            if (!columnExists(c, "drills", tierCol) && "tier".equals(tierCol)) {
                try (Statement st = c.createStatement()) {
                    st.executeUpdate("ALTER TABLE drills ADD COLUMN tier INTEGER NOT NULL DEFAULT 1;");
                }
            }

            // UPSERT: insert new or update existing drills by id.
            String sql = "INSERT INTO drills(id, title, " + bodyCol + ", " + tierCol + ") VALUES(?,?,?,?) " +
                    "ON CONFLICT(id) DO UPDATE SET " +
                    "  title=excluded.title, " + bodyCol + "=excluded." + bodyCol + ", " + tierCol + "=excluded." + tierCol;

            try (PreparedStatement ps = c.prepareStatement(sql)) {
                // Easy
                upsert(ps, 1,  "Easy 1", "the quick brown fox jumps over the lazy dog", 1);
                upsert(ps, 2,  "Easy 2", "hello world practice letters and spaces with care", 1);
                upsert(ps, 3,  "Easy 3", "type these easy words slowly to build rhythm and accuracy", 1);

                // Medium
                upsert(ps, 10, "Medium 1", "now add punctuation: commas, full stops, and question marks?", 2);
                upsert(ps, 11, "Medium 2", "mix CAPS and numbers: ABC 123 easy as one two three!", 2);
                upsert(ps, 12, "Medium 3", "don't forget apostrophes, it's important to use them correctly", 2);

                // Hard
                upsert(ps, 20, "Hard 1", "speed and precision matter; minimise errors to maximise your score.", 3);
                upsert(ps, 21, "Hard 2", "sphinx of black quartz, judge my vow: pack my box with five dozen liquor jugs.", 3);
                upsert(ps, 22, "Hard 3", "encyclopaedia and manoeuvre are spelt the Australian way; practice until it feels natural.", 3);
            }

            System.out.println("DrillSeeder: ensured baseline drills (upsert).");
        } catch (Exception e) {
            throw new RuntimeException("DrillSeeder failed", e);
        }
    }

    /** Insert or update one drill row. */
    private static void upsert(PreparedStatement ps, int id, String title, String body, int tier) throws SQLException {
        ps.setInt(1, id);
        ps.setString(2, title);
        ps.setString(3, body);
        ps.setInt(4, tier);
        ps.executeUpdate();
    }

    /** Return the first matching column name from candidates; fail if none found. */
    private static String findExistingColumn(Connection c, String table, String[] candidates) throws SQLException {
        HashSet<String> cols = tableColumns(c, table);
        for (String cand : candidates) if (cols.contains(cand.toLowerCase())) return cand;
        throw new SQLException("Table '" + table + "' missing expected columns " + Arrays.toString(candidates));
    }

    /** Return the first matching column name, or a default if none found. */
    private static String findExistingColumnOrDefault(Connection c, String table, String[] candidates, String def) throws SQLException {
        HashSet<String> cols = tableColumns(c, table);
        for (String cand : candidates) if (cols.contains(cand.toLowerCase())) return cand;
        return def;
    }

    /** True if the given column exists on the table. */
    private static boolean columnExists(Connection c, String table, String col) throws SQLException {
        return tableColumns(c, table).contains(col.toLowerCase());
    }

    /** List all column names for a table (lowercased). */
    private static HashSet<String> tableColumns(Connection c, String table) throws SQLException {
        HashSet<String> set = new HashSet<>();
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("PRAGMA table_info('" + table + "')")) {
            while (rs.next()) set.add(rs.getString("name").toLowerCase());
        }
        return set;
    }
}
