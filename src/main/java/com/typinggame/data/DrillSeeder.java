package com.typinggame.data;

import java.sql.*;

/**
 * Seeds baseline drills (Levels 1–10, 3 drills per level).
 * Fully hard-coded and idempotent (UPSERT on id).
 * SAFE: only adds 'level' column if it doesn't already exist.
 */
public final class DrillSeeder {
    private DrillSeeder() {}

    /** Ensure baseline drills exist (adds/updates rows). */
    public static void ensureBaselineDrills() {
        try (Connection c = Database.getConnection()) {
            // make sure 'level' exists, but only add it if missing
            if (!columnExists(c, "drills", "level")) {
                try (Statement st = c.createStatement()) {
                    st.executeUpdate("ALTER TABLE drills ADD COLUMN level INTEGER DEFAULT 1;");
                }
            }

            // also keep legacy 'tier' and 'level' aligned for existing rows
            try (Statement st = c.createStatement()) {
                st.executeUpdate("UPDATE drills SET level = COALESCE(level, tier, 1);");
                st.executeUpdate("UPDATE drills SET tier  = COALESCE(tier,  level, 1);");
            } catch (SQLException ignore) {}

            final String sql = """
                INSERT INTO drills(id, title, body, tier, level)
                VALUES(?,?,?,?,?)
                ON CONFLICT(id) DO UPDATE SET
                  title = excluded.title,
                  body  = excluded.body,
                  tier  = excluded.tier,
                  level = excluded.level
                """;

            try (PreparedStatement ps = c.prepareStatement(sql)) {
                // ---------- LEVEL 1 ----------
                upsert(ps, 11, "Level 1 – Home Row A", "fff jjj fff jjj fjfj fjfj", 1);
                upsert(ps, 12, "Level 1 – Home Row B", "asdf jkl; asdf jkl; aaaa ssss dddd ffff", 1);
                upsert(ps, 13, "Level 1 – Words",      "sad flask dad salad ask fall desk", 1);

                // ---------- LEVEL 2 ----------
                upsert(ps, 21, "Level 2 – Numbers",     "123 456 789 0 12 34 56 78 90", 2);
                upsert(ps, 22, "Level 2 – Punctuation", "Now, add commas, periods. Do you see?", 2);
                upsert(ps, 23, "Level 2 – Capitals",    "CAPS Lock Practice: The Quick Brown Fox.", 2);

                // ---------- LEVEL 3 ----------
                upsert(ps, 31, "Level 3 – Sentences",   "Typing practice improves both speed and accuracy.", 3);
                upsert(ps, 32, "Level 3 – Mixed Case",  "Type These Words Exactly As You See Them.", 3);
                upsert(ps, 33, "Level 3 – Challenge",   "Stay calm and keep typing under pressure!", 3);

                // ---------- LEVEL 4 ----------
                upsert(ps, 41, "Level 4 – Short Paragraph",
                        "Typing steadily builds muscle memory and confidence. Focus on smooth keystrokes.", 4);
                upsert(ps, 42, "Level 4 – Symbols",     "Try symbols: @#$%^&*()_+ =- [] {}", 4);
                upsert(ps, 43, "Level 4 – Accuracy Drill",
                        "Slow is smooth, smooth is fast. Type cleanly to reduce mistakes.", 4);

                // ---------- LEVEL 5 ----------
                upsert(ps, 51, "Level 5 – Long Words",
                        "encyclopaedia manoeuvre miscellaneous acknowledgement responsibility", 5);
                upsert(ps, 52, "Level 5 – Rhythm",
                        "Tap the keys with rhythm and flow; maintain consistent finger motion.", 5);
                upsert(ps, 53, "Level 5 – Sentences",
                        "Accuracy first, speed second; both improve with daily deliberate practice.", 5);

                // ---------- LEVEL 6 ----------
                upsert(ps, 61, "Level 6 – Speed Burst",
                        "Type faster now! Push your WPM beyond comfort but avoid chaos.", 6);
                upsert(ps, 62, "Level 6 – Punctuation Practice",
                        "Commas, semicolons; and full stops. Keep spacing neat.", 6);
                upsert(ps, 63, "Level 6 – Numbers & Words",
                        "I typed 50 words in 60 seconds with 98 percent accuracy.", 6);

                // ---------- LEVEL 7 ----------
                upsert(ps, 71, "Level 7 – Mixed Drill",
                        "The quick brown fox jumps over 13 lazy dogs and 2 sleepy cats.", 7);
                upsert(ps, 72, "Level 7 – Quotes",
                        "\"Practice makes permanent,\" said the wise instructor.", 7);
                upsert(ps, 73, "Level 7 – Balance",
                        "Focus equally on speed, accuracy, and endurance.", 7);

                // ---------- LEVEL 8 ----------
                upsert(ps, 81, "Level 8 – Long Passage",
                        "Typing is a skill developed through repetition and conscious correction of mistakes.", 8);
                upsert(ps, 82, "Level 8 – Variety",
                        "Switch between words, numbers, and punctuation for dynamic flow.", 8);
                upsert(ps, 83, "Level 8 – Timing",
                        "Keep a steady rhythm; avoid rushing or slowing excessively.", 8);

                // ---------- LEVEL 9 ----------
                upsert(ps, 91, "Level 9 – Complex Text",
                        "While perfection is unattainable, consistent improvement defines mastery.", 9);
                upsert(ps, 92, "Level 9 – Challenge Paragraph",
                        "Students typing daily for ten minutes improve speed by twenty percent.", 9);
                upsert(ps, 93, "Level 9 – Creative Writing",
                        "Compose short creative sentences while maintaining proper technique.", 9);

                // ---------- LEVEL 10 ----------
                upsert(ps, 101, "Level 10 – Final Challenge",
                        "This is your final typing test; deliver speed, precision, and confidence.", 10);
                upsert(ps, 102, "Level 10 – Mastery",
                        "Typing fluently allows ideas to flow freely from mind to screen.", 10);
                upsert(ps, 103, "Level 10 – Endurance",
                        "Sustain high WPM for a full minute without dropping accuracy.", 10);
            }

            System.out.println("DrillSeeder: seeded 10 levels × 3 drills each.");
        } catch (SQLException e) {
            throw new RuntimeException("DrillSeeder failed", e);
        }
    }

    private static void upsert(PreparedStatement ps, int id, String title, String body, int level) throws SQLException {
        ps.setInt(1, id);
        ps.setString(2, title);
        ps.setString(3, body);
        ps.setInt(4, level); // tier (mirror for legacy)
        ps.setInt(5, level); // level
        ps.executeUpdate();
    }

    private static boolean columnExists(Connection c, String table, String col) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("PRAGMA table_info('" + table + "')");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String name = rs.getString("name");
                if (name != null && name.equalsIgnoreCase(col)) return true;
            }
            return false;
        }
    }
}
