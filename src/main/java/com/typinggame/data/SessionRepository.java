package com.typinggame.data;

import com.typinggame.model.Session;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple repository for typing sessions.
 * - Uses try-with-resources so DB resources are closed.
 * - Wraps SQL errors in RuntimeException.
 */
public class SessionRepository {

    /** Insert a session and return it with the generated id (if any). */
    public Session insert(Session s) {
        String sql = """
            INSERT INTO sessions(user_id, drill_id, wpm, accuracy, score, typed_chars, duration_seconds, started_at)
            VALUES(?,?,?,?,?,?,?,?)
            """;
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, s.userId);
            ps.setInt(2, s.drillId);
            ps.setDouble(3, s.wpm);
            ps.setDouble(4, s.accuracy);
            ps.setDouble(5, s.score);
            ps.setInt(6, s.typedChars);
            ps.setDouble(7, s.durationSeconds);
            ps.setString(8, s.startedAt.toString()); // store ISO-8601 string

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                Integer id = null;
                if (keys.next()) id = keys.getInt(1);
                // return a copy that includes the new id
                return new Session(id, s.userId, s.drillId, s.wpm, s.accuracy, s.typedChars, s.durationSeconds, s.startedAt);
            }
        } catch (SQLException e) {
            throw new RuntimeException("insert session failed", e);
        }
    }

    /** Best (max) score for a user, or null if no sessions. */
    public Double bestScoreForUser(int userId) {
        String sql = "SELECT MAX(score) AS best FROM sessions WHERE user_id = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double v = rs.getDouble("best");
                    return rs.wasNull() ? null : v; // handle no rows / NULL
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("bestScoreForUser failed", e);
        }
    }

    /** Count sessions by a user for drills in a specific tier. */
    public int countSessionsInTier(int userId, int tier) {
        String sql = """
            SELECT COUNT(*) AS cnt
            FROM sessions s
            JOIN drills d ON d.id = s.drill_id
            WHERE s.user_id = ? AND d.tier = ?
            """;
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, tier);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("cnt") : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("countSessionsInTier failed", e);
        }
    }

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

    /**
     * Best single session per user (global), sorted by score.
     * Uses a CTE to pick each user's max score.
     */
    public List<LeaderboardRow> topByBestScore(int limit) {
        String sql = """
            WITH best AS (
              SELECT user_id, MAX(score) AS best_score
              FROM sessions
              GROUP BY user_id
            )
            SELECT u.display_name AS name, s.wpm, s.accuracy, s.score
            FROM best b
            JOIN sessions s ON s.user_id = b.user_id AND s.score = b.best_score
            JOIN users u    ON u.id = b.user_id
            ORDER BY s.score DESC
            LIMIT ?
            """;
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
}
