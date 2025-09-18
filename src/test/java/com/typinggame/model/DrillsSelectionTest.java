package com.typinggame.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for basic drill selection/lookup behaviors.
 * Validates get-by-id, tier filtering, and missing IDs.
 * <p>
 * [Adriel – Sep 18 2025]
 */
public class DrillsSelectionTest {

    // ---- Minimal domain scaffolding (fakes + model) ----
    record Drill(int id, String title, String content, int tier) {}

    interface DrillRepository {
        Drill getById(int id);
        List<Drill> getByTier(int tier);
        List<Drill> all();
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

    static class DrillService {
        private final DrillRepository repo;
        DrillService(DrillRepository repo) { this.repo = repo; }
        public Optional<Drill> get(int id) {
            try { return Optional.ofNullable(repo.getById(id)); }
            catch (Exception e) { return Optional.empty(); }
        }
        public List<Drill> byTier(int tier) { return repo.getByTier(tier); }
    }

    // ---- Test data ----
    DrillService drills;

    @BeforeEach
    void setup() {
        drills = new DrillService(new FakeDrillRepo(List.of(
                new Drill(1, "Home Row", "asdf jkl;", 1),
                new Drill(2, "Numbers", "123 456", 2),
                new Drill(3, "Punctuation", ",.;!?", 2)
        )));
    }

    @Test
    public void testGetExistingDrillById() {
        var d = drills.get(1);
        assertTrue(d.isPresent());
        assertEquals("Home Row", d.get().title());
    }

    @Test
    public void testGetMissingDrillReturnsEmpty() {
        assertTrue(drills.get(999).isEmpty());
    }

    @Test
    public void testGetByTierFiltersCorrectly() {
        var t2 = drills.byTier(2);
        assertEquals(2, t2.size());
        assertTrue(t2.stream().allMatch(d -> d.tier() == 2));
    }
}
