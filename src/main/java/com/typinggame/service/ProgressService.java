package com.typinggame.service;

import com.typinggame.config.Config;
import com.typinggame.data.DrillRepository;
import com.typinggame.data.SessionRepository;

public class ProgressService {
    private final SessionRepository sessions;
    private final DrillRepository drills;

    public ProgressService(SessionRepository sessions, DrillRepository drills){
        this.sessions = sessions;
        this.drills = drills;
    }

    public int currentUnlockedTier(int userId){
        int maxTier = drills.maxTier();
        double best = sessions.bestScoreForUser(userId) == null ? 0.0 : sessions.bestScoreForUser(userId);
        int tier = 1;
        while (tier < maxTier) {
            boolean byScore = best >= Config.UNLOCK_SCORE_THRESHOLD;
            boolean bySessions = sessions.countSessionsInTier(userId, tier) >= Config.SESSIONS_TO_UNLOCK_NEXT;
            if (byScore || bySessions) tier++; else break;
        }
        return tier;
    }
}
