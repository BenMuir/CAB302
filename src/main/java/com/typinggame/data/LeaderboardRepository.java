package com.typinggame.data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Read-only leaderboard queries using the sessions table.
 * - Global best (per user)
 * - Per-drill best (per user)
 */
public class LeaderboardRepository {

    /** Simple DTO for leaderboard rows. */
    public static class LeaderboardRow {
        public final String name;
        public final double wpm;
        public final double accuracy;
        public final double score;
        public LeaderboardRow(String name, double wpm, double accuracy, double score){
            this.name = name; this.wpm = wpm; this.accuracy = accuracy; this.score = score;
        }
    }

    /** Best single session per user across all drills. Sorted by score, then WPM, then accuracy. */
    public List<LeaderboardRow> topByBestScore(int limit) {
        String sql = ""
                + "WITH best AS ("
                + "  SELECT user_id, MAX(score) AS best_score"
                + "  FROM sessions"
                + "  GROUP BY user_id"
                + ") "
                + "SELECT u.username AS name, s.wpm, s.accuracy, s.score "
                + "FROM sessions s "
                + "JOIN best b ON b.user_id = s.user_id AND b.best_score = s.score "
                + "JOIN users u ON u.id = s.user_id "
                + "ORDER BY s.score DESC, s.wpm DESC, s.accuracy DESC "
                + "LIMIT ?";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                List<LeaderboardRow> out = new ArrayList<>();
                while (rs.next()) {
                    out.add(new LeaderboardRow(
                            rs.getString("name"),
                            rs.getDouble("wpm"),
                            rs.getDouble("accuracy"),
                            rs.getDouble("score")
                    ));
                }
                return out;
            }
        } catch (SQLException e) {
            throw new RuntimeException("topByBestScore failed", e);
        }
    }

    /** Best single session per user for a given drill. Sorted by score, then WPM, then accuracy. */
    public List<LeaderboardRow> topByBestScoreForDrill(int drillId, int limit) {
        String sql = ""
                + "WITH best AS ("
                + "  SELECT user_id, MAX(score) AS best_score"
                + "  FROM sessions"
                + "  WHERE drill_id = ?"
                + "  GROUP BY user_id"
                + ") "
                + "SELECT u.username AS name, s.wpm, s.accuracy, s.score "
                + "FROM sessions s "
                + "JOIN best b ON b.user_id = s.user_id AND b.best_score = s.score "
                + "JOIN users u ON u.id = s.user_id "
                + "WHERE s.drill_id = ? "
                + "ORDER BY s.score DESC, s.wpm DESC, s.accuracy DESC "
                + "LIMIT ?";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, drillId);
            ps.setInt(2, drillId);
            ps.setInt(3, limit);

            try (ResultSet rs = ps.executeQuery()) {
                List<LeaderboardRow> out = new ArrayList<>();
                while (rs.next()) {
                    out.add(new LeaderboardRow(
                            rs.getString("name"),
                            rs.getDouble("wpm"),
                            rs.getDouble("accuracy"),
                            rs.getDouble("score")
                    ));
                }
                return out;
            }
        } catch (SQLException e) {
            throw new RuntimeException("topByBestScoreForDrill failed", e);
        }
    }
}
