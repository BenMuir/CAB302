package com.typinggame.service;

import com.typinggame.data.DrillRepository;
import com.typinggame.data.SessionRepository;
import com.typinggame.model.Drill;
import com.typinggame.model.Session;

import java.time.Instant;

/**
 * Records typing sessions and computes WPM/accuracy.
 */
public class SessionService {
    private final SessionRepository sessions;
    private final DrillRepository drills;

    public SessionService(SessionRepository sessions, DrillRepository drills){
        this.sessions = sessions;
        this.drills = drills;
    }

    /** Small container for derived metrics. */
    private record Metrics(double wpm, double accuracy, int typedLen) {}

    /**
     * Save a session for a user on a drill.
     * - Looks up the drill by id (throws if not found).
     * - Computes accuracy and WPM from the typed text and elapsed time.
     * - Persists the session and returns the saved copy (with id).
     */
    public Session recordSession(int userId, int drillId, String typed, double elapsedSeconds){
        // Basic input validation (fail fast with clear messages)
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user id: " + userId);
        }
        if (elapsedSeconds <= 0) {
            throw new IllegalArgumentException("elapsedSeconds must be > 0, was " + elapsedSeconds);
        }

        // Find the drill (direct by id; no full-table scan)
        Drill d = drills.findById(drillId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown drill id: " + drillId));

        // Calculate metrics in a single, testable step
        Metrics m = computeMetrics(d, typed, elapsedSeconds);

        // Create and insert
        Session s = new Session(
                null,                 // id (null before insert)
                userId,
                drillId,
                m.wpm(),
                m.accuracy(),
                m.typedLen(),
                elapsedSeconds,
                Instant.now()
        );

        return sessions.insert(s);
    }

    /** Derive metrics with safe handling of null/blank typed text. */
    private Metrics computeMetrics(Drill drill, String typed, double elapsedSeconds) {
        String safeTyped = (typed == null) ? "" : typed;
        int typedLen = safeTyped.length();
        double accuracy = CalcService.accuracy(drill.body, safeTyped);
        double wpm = CalcService.wpm(typedLen, elapsedSeconds);
        return new Metrics(wpm, accuracy, typedLen);
    }
}
