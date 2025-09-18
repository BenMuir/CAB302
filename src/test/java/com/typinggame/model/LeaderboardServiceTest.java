package com.typinggame.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for leaderboard ordering, ties, and top-N slicing.
 * Assumes composite score = WPM × Accuracy (Accuracy in 0–1).
 * Tie-breakers: higher WPM, then higher Accuracy, then earlier timestamp.
 * <p>
 * [Adriel – Sep 18 2025]
 */
public class LeaderboardServiceTest {

    // ---- Minimal domain scaffolding (fakes + model) ----
    record ScoreEntry(String username, int drillId, double wpm, double accuracy, long timestamp) {}

    interface LeaderboardRepository {
        void saveScore(ScoreEntry e);
        List<ScoreEntry> all();
    }

    static class FakeLeaderboardRepo implements LeaderboardRepository {
        final List<ScoreEntry> store = new ArrayList<>();
        public void saveScore(ScoreEntry e) { store.add(e); }
        public List<ScoreEntry> all() { return new ArrayList<>(store); }
    }

    static class LeaderboardService {
        private final LeaderboardRepository repo;
        LeaderboardService(LeaderboardRepository repo) { this.repo = repo; }

        double score(ScoreEntry e) { return e.wpm * e.accuracy; }

        static final double EPS = 1e-9;

        /** Sorted by composite desc, then WPM desc, then Acc desc, then timestamp asc. */
        List<ScoreEntry> topN(int n) {
            return repo.all().stream()
                    .sorted((a, b) -> {
                        double sa = score(a), sb = score(b);
                        if (Math.abs(sa - sb) > EPS) return Double.compare(sb, sa); // composite desc
                        int byWpm = Double.compare(b.wpm, a.wpm);                   // WPM desc
                        if (byWpm != 0) return byWpm;
                        int byAcc = Double.compare(b.accuracy, a.accuracy);         // Accuracy desc
                        if (byAcc != 0) return byAcc;
                        return Long.compare(a.timestamp, b.timestamp);              // earlier first
                    })
                    .limit(n)
                    .toList();
        }
    }

    // ---- Test data ----
    FakeLeaderboardRepo repo;
    LeaderboardService svc;

    @BeforeEach
    void setup() {
        repo = new FakeLeaderboardRepo();
        svc = new LeaderboardService(repo);

        // alice: 80*0.92 = 73.6 (t=1000)
        repo.saveScore(new ScoreEntry("alice", 1, 80, 0.92, 1000));
        // bob:   78*0.99 = 77.22 (t= 900)  -> should rank 1st initially
        repo.saveScore(new ScoreEntry("bob",   1, 78, 0.99,  900));
        // cara:  90*0.70 = 63.0  (t=1100)
        repo.saveScore(new ScoreEntry("cara",  2, 90, 0.70, 1100));
        // dan:   80*0.92 = 73.6 (t= 800)  -> tie with alice; earlier time should rank higher
        repo.saveScore(new ScoreEntry("dan",   2, 80, 0.92,  800));
    }

    @Test
    public void testOrderByCompositeScore() {
        var top = svc.topN(10);
        assertEquals(List.of("bob", "dan", "alice", "cara"),
                top.stream().map(s -> s.username).toList());
    }

    @Test
    public void testTieBreakersWpmAccTimestamp() {
        // Exact tie: 96*0.5 = 48.0 and 48*1.0 = 48.0 (0.5 and 1.0 are exact in binary)
        repo.saveScore(new ScoreEntry("erin", 1, 96, 0.50, 700)); // composite = 48.0, WPM=96
        repo.saveScore(new ScoreEntry("finn", 1, 48, 1.00, 600)); // composite = 48.0, WPM=48

        var top = svc.topN(10);
        var order = top.stream().map(s -> s.username).toList();
        assertTrue(order.indexOf("erin") < order.indexOf("finn"),
                "Higher WPM should break tie when composite scores are equal");
    }

    @Test
    public void testTopNReturnsCorrectSlice() {
        var top2 = svc.topN(2);
        assertEquals(2, top2.size());
        assertEquals("bob", top2.get(0).username);
        assertEquals("dan", top2.get(1).username);
    }
}
