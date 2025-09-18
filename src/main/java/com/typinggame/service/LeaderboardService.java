package com.typinggame.service;

import com.typinggame.data.LeaderboardRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Leaderboard read service (maps repo rows to simple DTOs).
 */
public class LeaderboardService {
    private final LeaderboardRepository repo;

    /** Simple DTO for UI or controllers. */
    public static class Row {
        public final String name;
        public final double wpm;
        public final double accuracy;
        public final double score;
        public Row(String name, double wpm, double accuracy, double score){
            this.name = name; this.wpm = wpm; this.accuracy = accuracy; this.score = score;
        }
    }

    public LeaderboardService(LeaderboardRepository repo){
        this.repo = repo;
    }

    /** Global leaderboard (best per user across all drills). */
    public List<Row> topByBestScore(int limit){
        return repo.topByBestScore(limit).stream()
                .map(r -> new Row(r.name, r.wpm, r.accuracy, r.score))
                .collect(Collectors.toList());
    }

    /** Per-drill leaderboard (best per user for one drill). */
    public List<Row> topByBestScoreForDrill(int drillId, int limit){
        return repo.topByBestScoreForDrill(drillId, limit).stream()
                .map(r -> new Row(r.name, r.wpm, r.accuracy, r.score))
                .collect(Collectors.toList());
    }
}
