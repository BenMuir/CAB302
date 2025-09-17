package com.typinggame.data;

import java.sql.*;
import java.util.Arrays;
import java.util.HashSet;

public final class DrillSeeder {
    private DrillSeeder() {}

    public static void ensureBaselineDrills() {
        try (Connection c = Database.getConnection()) {
            String bodyCol = findExistingColumn(c, "drills",
                    new String[]{"body", "sentence", "text", "content"});
            String tierCol = findExistingColumnOrDefault(c, "drills",
                    new String[]{"tier", "difficulty", "difficulty_tier", "difficultyTier"}, "tier");

            // Add tier column if missing and we’re allowed to call it 'tier'
            if (!columnExists(c, "drills", tierCol) && "tier".equals(tierCol)) {
                try (Statement st = c.createStatement()) {
                    st.executeUpdate("ALTER TABLE drills ADD COLUMN tier INTEGER NOT NULL DEFAULT 1;");
                }
            }

            // Use SQLite UPSERT so existing rows are corrected
            String sql = "INSERT INTO drills(id, title, " + bodyCol + ", " + tierCol + ") VALUES(?,?,?,?) " +
                    "ON CONFLICT(id) DO UPDATE SET " +
                    "  title=excluded.title, " + bodyCol + "=excluded." + bodyCol + ", " + tierCol + "=excluded." + tierCol;

            try (PreparedStatement ps = c.prepareStatement(sql)) {
                upsert(ps, 1,  "Easy 1",   "The quick brown fox jumps over the lazy dog.", 1);
                upsert(ps, 2,  "Easy 2",   "hello world hello world practice letters and spaces", 1);
                upsert(ps, 3,  "Easy 3",   "type these easy words to build rhythm and accuracy", 1);

                upsert(ps, 10, "Medium 1", "now add punctuation: commas, periods, and question marks?", 2);
                upsert(ps, 11, "Medium 2", "mix CAPS and numbers: ABC 123 easy as one two three!",       2);

                upsert(ps, 20, "Hard 1",   "speed and precision matter; minimize errors to maximize score.", 3);
                upsert(ps, 21, "Hard 2",   "sphinx of black quartz, judge my vow: pack my box with five dozen liquor jugs.", 3);
            }
            System.out.println("DrillSeeder: ensured baseline drills (upsert).");
        } catch (Exception e) {
            throw new RuntimeException("DrillSeeder failed", e);
        }
    }

    private static void upsert(PreparedStatement ps, int id, String title, String body, int tier) throws SQLException {
        ps.setInt(1, id);
        ps.setString(2, title);
        ps.setString(3, body);
        ps.setInt(4, tier);
        ps.executeUpdate();
    }

    private static String findExistingColumn(Connection c, String table, String[] candidates) throws SQLException {
        HashSet<String> cols = tableColumns(c, table);
        for (String cand : candidates) if (cols.contains(cand.toLowerCase())) return cand;
        throw new SQLException("Table '" + table + "' missing expected columns " + Arrays.toString(candidates));
    }

    private static String findExistingColumnOrDefault(Connection c, String table, String[] candidates, String def) throws SQLException {
        HashSet<String> cols = tableColumns(c, table);
        for (String cand : candidates) if (cols.contains(cand.toLowerCase())) return cand;
        return def;
    }

    private static boolean columnExists(Connection c, String table, String col) throws SQLException {
        return tableColumns(c, table).contains(col.toLowerCase());
    }

    private static HashSet<String> tableColumns(Connection c, String table) throws SQLException {
        HashSet<String> set = new HashSet<>();
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("PRAGMA table_info('" + table + "')")) {
            while (rs.next()) set.add(rs.getString("name").toLowerCase());
        }
        return set;
    }
}
