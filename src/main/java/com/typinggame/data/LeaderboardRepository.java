package com.typinggame.data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Read-only queries for leaderboards.
 * Supports global and per-drill leaderboards using the sessions table.
 */
public class LeaderboardRepository {

    public static class LeaderboardRow {
        public final String name;
        public final double wpm;
        public final double accuracy;
        public final double score;
        public LeaderboardRow(String name, double wpm, double accuracy, double score){
            this.name = name; this.wpm = wpm; this.accuracy = accuracy; this.score = score;
        }
    }

    /** Top best-single-session scores across ALL drills (one row per user by their best score). */
    public List<LeaderboardRow> topByBestScore(int limit) {
        String sql = "WITH best AS ("
                + "  SELECT user_id, MAX(score) AS best_score FROM sessions GROUP BY user_id"
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

    /** Top best-single-session scores for a specific drill (one row per user by their best score in that drill). */
    public List<LeaderboardRow> topByBestScoreForDrill(int drillId, int limit) {
        String sql = "WITH best AS ("
                + "  SELECT user_id, MAX(score) AS best_score FROM sessions WHERE drill_id = ? GROUP BY user_id"
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
