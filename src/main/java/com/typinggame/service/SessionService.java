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

    /**
     * Save a session for a user on a drill.
     * - Looks up the drill by id (throws if not found).
     * - Computes accuracy and WPM from the typed text and elapsed time.
     * - Persists the session and returns the saved copy (with id).
     */
    public Session recordSession(int userId, int drillId, String typed, double elapsedSeconds){
        // Find the drill (simple scan; OK for small data sets)
        Drill d = drills.findUpToTier(Integer.MAX_VALUE).stream()
                .filter(dr -> dr.id == drillId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown drill id: " + drillId));

        // Calculate metrics
        double acc = CalcService.accuracy(d.body, typed);
        int typedLen = (typed == null) ? 0 : typed.length();
        double wpm = CalcService.wpm(typedLen, elapsedSeconds);

        // Create and insert
        Session s = new Session(
                null,                 // id (null before insert)
                userId,
                drillId,
                wpm,
                acc,
                typedLen,
                elapsedSeconds,
                Instant.now()
        );

        return sessions.insert(s);
    }
}
