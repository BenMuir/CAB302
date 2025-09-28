package com.typinggame.service;

import com.typinggame.data.DrillRepository;
import com.typinggame.model.Drill;
import java.util.List;

public class DrillService {
    private final DrillRepository repo;
    private final ProgressService progress;

    public DrillService(DrillRepository repo, ProgressService progress){
        this.repo = repo;
        this.progress = progress;
    }

    /** Return drills up to the user’s currently unlocked tier. */
    public List<Drill> listUnlocked(int userId){
        int tier = progress.currentUnlockedTier(userId);
        return repo.findUpToTier(tier);
    }

    // --- NEW: custom drill passthroughs ---
    public int insertCustom(Drill d) { return repo.insertCustom(d); }
    public List<Drill> findCustom() { return repo.findCustom(); }
    public boolean deleteCustom(int id) { return repo.deleteCustom(id); }
}
