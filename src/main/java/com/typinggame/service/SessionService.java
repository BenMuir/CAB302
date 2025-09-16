package com.typinggame.service;

import com.typinggame.data.DrillRepository;
import com.typinggame.data.SessionRepository;
import com.typinggame.model.Drill;
import com.typinggame.model.Session;

import java.time.Instant;
import java.util.List;

public class SessionService {
    private final SessionRepository sessions;
    private final DrillRepository drills;

    public SessionService(SessionRepository sessions, DrillRepository drills){
        this.sessions = sessions;
        this.drills = drills;
    }

    public Session recordSession(int userId, int drillId, String typed, double elapsedSeconds){
        Drill d = drills.findUpToTier(Integer.MAX_VALUE).stream() // quick fetch; small dataset
                .filter(dr -> dr.id == drillId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown drill id: " + drillId));

        double acc = CalcService.accuracy(d.body, typed);
        double wpm = CalcService.wpm(typed == null ? 0 : typed.length(), elapsedSeconds);

        Session s = new Session(null, userId, drillId, wpm, acc,
                typed == null ? 0 : typed.length(), elapsedSeconds, Instant.now());

        return sessions.insert(s);
    }
}
