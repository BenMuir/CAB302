package com.typinggame.model;

import java.io.Serializable;
import java.time.Instant;

/**
 * Represents a typing session result.
 * Immutable after creation, except id may be null until DB insert.
 */
public class Session implements Serializable {
    public final Integer id;             // DB auto-increment id (null before insert)
    public final int userId;             // user foreign key
    public final int drillId;            // drill foreign key
    public final double wpm;             // words per minute
    public final double accuracy;        // accuracy as 0–100 %
    public final double score;           // derived: WPM × Accuracy
    public final int typedChars;         // number of characters typed
    public final double durationSeconds; // session duration in seconds
    public final Instant startedAt;      // timestamp when started

    public Session(Integer id, int userId, int drillId, double wpm, double accuracy,
                   int typedChars, double durationSeconds, Instant startedAt){
        this.id = id;
        this.userId = userId;
        this.drillId = drillId;
        this.wpm = wpm;
        this.accuracy = accuracy;
        this.score = wpm * accuracy; // score computed on creation
        this.typedChars = typedChars;
        this.durationSeconds = durationSeconds;
        this.startedAt = startedAt;
    }
}
