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

            // If tier column doesn't exist, try to add it.
            if (!columnExists(c, "drills", tierCol) && "tier".equals(tierCol)) {
                try (Statement st = c.createStatement()) {
                    st.executeUpdate("ALTER TABLE drills ADD COLUMN tier INTEGER NOT NULL DEFAULT 1;");
                }
            }

            String sql = "INSERT OR IGNORE INTO drills(id, title, " + bodyCol + ", " + tierCol + ") VALUES(?,?,?,?)";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                insert(ps, 1, "Easy 1",   "cat dog sun run fun", 1);
                insert(ps, 2, "Easy 2",   "time day night light bright", 1);
                insert(ps, 3, "Easy 3",   "red blue green yellow orange", 1);
                insert(ps, 4, "Medium 1", "The quick brown fox jumps over the lazy dog.", 2);
                insert(ps, 5, "Medium 2", "Typing fast is fun, but accuracy is even better!", 2);
                insert(ps, 6, "Hard 1",   "Complexity arises when we type: symbols, commas, and quotes—yet fluency must remain.", 3);
            }
            System.out.println("DrillSeeder: ensured baseline drills.");
        } catch (Exception e) {
            throw new RuntimeException("DrillSeeder failed", e);
        }
    }

    private static void insert(PreparedStatement ps, int id, String title, String body, int tier) throws SQLException {
        ps.setInt(1, id);
        ps.setString(2, title);
        ps.setString(3, body);
        ps.setInt(4, tier);
        ps.addBatch();
        ps.executeBatch();
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
