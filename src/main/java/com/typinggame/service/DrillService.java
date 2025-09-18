package com.typinggame.service;

import com.typinggame.data.DrillRepository;
import com.typinggame.model.Drill;

import java.util.List;

/**
 * Drill-related operations (read side).
 */
public class DrillService {
    private final DrillRepository repo;
    private final ProgressService progress;

    public DrillService(DrillRepository repo, ProgressService progress){
        this.repo = repo;
        this.progress = progress;
    }

    /**
     * Return drills up to the user’s currently unlocked tier.
     */
    public List<Drill> listUnlocked(int userId){
        int tier = progress.currentUnlockedTier(userId);
        return repo.findUpToTier(tier);
    }
}
