package com.typinggame.service;

import com.typinggame.data.SessionRepository;

import java.util.List;
import java.util.stream.Collectors;

public class LeaderboardService {
    private final SessionRepository repo;

    public static class Row {
        public final String name;
        public final double wpm;
        public final double accuracy;
        public final double score;
        public Row(String name, double wpm, double accuracy, double score){
            this.name = name; this.wpm = wpm; this.accuracy = accuracy; this.score = score;
        }
    }

    public LeaderboardService(SessionRepository repo){
        this.repo = repo;
    }

    public List<Row> topByBestScore(int limit){
        return repo.topByBestScore(limit).stream()
                .map(r -> new Row(r.name, r.wpm, r.accuracy, r.score))
                .collect(Collectors.toList());
    }
}
