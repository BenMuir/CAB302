package com.typinggame.model;

import java.io.Serializable;
import java.time.Instant;

public class Session implements Serializable {
    public final Integer id;           // database autoincrement (nullable before insert)
    public final int userId;
    public final int drillId;
    public final double wpm;
    public final double accuracy;      // 0..100
    public final double score;         // WPM × Accuracy
    public final int typedChars;
    public final double durationSeconds;
    public final Instant startedAt;

    public Session(Integer id, int userId, int drillId, double wpm, double accuracy,
                   int typedChars, double durationSeconds, Instant startedAt){
        this.id = id;
        this.userId = userId;
        this.drillId = drillId;
        this.wpm = wpm;
        this.accuracy = accuracy;
        this.score = wpm * accuracy;
        this.typedChars = typedChars;
        this.durationSeconds = durationSeconds;
        this.startedAt = startedAt;
    }
}
