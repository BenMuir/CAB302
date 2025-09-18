package com.typinggame.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for drill progression gating and user-specific unlocks.
 * Implements "complete ALL Tier 1 before Tier 2 unlocks" rule.
 * <p>
 * [Adriel – Sep 18 2025]
 */
public class DrillProgressionTest {

    // ---- Minimal domain scaffolding (fakes + model) ----
    record Drill(int id, String title, String content, int tier) {}

    interface DrillRepository {
        Drill getById(int id);
        List<Drill> getByTier(int tier);
        List<Drill> all();
    }

    interface ProgressRepository {
        Set<Integer> getCompletedDrillIds(String username);
        void markCompleted(String username, int drillId);
    }

    static class FakeDrillRepo implements DrillRepository {
        private final Map<Integer, Drill> map = new HashMap<>();
        FakeDrillRepo(List<Drill> seed) { seed.forEach(d -> map.put(d.id(), d)); }
        public Drill getById(int id) { return map.get(id); }
        public List<Drill> getByTier(int tier) {
            return map.values().stream().filter(d -> d.tier() == tier).toList();
        }
        public List<Drill> all() { return new ArrayList<>(map.values()); }
    }

    static class FakeProgressRepo implements ProgressRepository {
        private final Map<String, Set<Integer>> completed = new HashMap<>();
        public Set<Integer> getCompletedDrillIds(String user) {
            return completed.getOrDefault(user, Collections.emptySet());
        }
        public void markCompleted(String user, int drillId) {
            completed.computeIfAbsent(user, u -> new HashSet<>()).add(drillId);
        }
    }

    static class ProgressionService {
        private final DrillRepository drills;
        private final ProgressRepository progress;
        ProgressionService(DrillRepository drills, ProgressRepository progress) {
            this.drills = drills; this.progress = progress;
        }

        public boolean allTierCompleted(String user, int tier) {
            var tierIds = drills.getByTier(tier).stream().map(Drill::id).toList();
            var done = progress.getCompletedDrillIds(user);
            return !tierIds.isEmpty() && done.containsAll(tierIds);
        }

        /** unlocked = all Tier 1; plus Tier k unlocked iff all tiers < k completed */
        public Set<Integer> unlockedFor(String user) {
            Set<Integer> u = new HashSet<>();
            int maxTier = drills.all().stream().mapToInt(Drill::tier).max().orElse(1);
            for (int tier = 1; tier <= maxTier; tier++) {
                if (tier == 1 || allLowerTiersComplete(user, tier)) {
                    for (var d : drills.getByTier(tier)) u.add(d.id());
                }
            }
            return u;
        }

        private boolean allLowerTiersComplete(String user, int tier) {
            for (int t = 1; t < tier; t++) if (!allTierCompleted(user, t)) return false;
            return true;
        }

        public void markComplete(String user, int drillId) {
            progress.markCompleted(user, drillId);
        }
    }

    // ---- Test data ----
    ProgressionService svc;

    @BeforeEach
    void setup() {
        var repo = new FakeDrillRepo(List.of(
                new Drill(1, "D1", "", 1),
                new Drill(2, "D2", "", 1),
                new Drill(3, "D3", "", 2),
                new Drill(4, "D4", "", 2),
                new Drill(5, "D5", "", 3)
        ));
        svc = new ProgressionService(repo, new FakeProgressRepo());
    }

    @Test
    public void testTier1UnlockedByDefault() {
        var unlocked = svc.unlockedFor("alice");
        assertTrue(unlocked.containsAll(Set.of(1, 2)));
        assertFalse(unlocked.contains(3));
    }

    @Test
    public void testTier2LockedUntilAllTier1Complete() {
        svc.markComplete("alice", 1);
        assertFalse(svc.unlockedFor("alice").contains(3), "Still locked until ALL Tier 1 done");
        svc.markComplete("alice", 2);
        assertTrue(svc.unlockedFor("alice").containsAll(Set.of(3, 4)));
    }

    @Test
    public void testUnlocksAreUserSpecific() {
        svc.markComplete("alice", 1);
        svc.markComplete("alice", 2);
        assertTrue(svc.unlockedFor("alice").contains(3));
        assertFalse(svc.unlockedFor("bob").contains(3));
    }

    @Test
    public void testReCompletingDrillIsIdempotent() {
        svc.markComplete("alice", 1);
        svc.markComplete("alice", 1); // again
        assertFalse(svc.unlockedFor("alice").contains(3)); // still need drill 2
    }
}
