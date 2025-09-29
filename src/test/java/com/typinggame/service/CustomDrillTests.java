package com.typinggame.service;

import com.typinggame.data.DrillRepository;
import com.typinggame.model.Drill;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomDrillTests {

    private DrillRepository drills;

    @BeforeAll
    static void initDb() {
        // Ensure tables exist before any tests run
        com.typinggame.data.Database.init();
    }

    @BeforeEach
    void setup() {
        drills = new DrillRepository();
        drills.clearAll(); // wipe table between tests
    }

    @Test
    @DisplayName("Insert custom drill should persist to database")
    void testInsertCustomDrill() {
        Drill d = new Drill(0, "My Custom Drill", "abc def ghi", 2);
        drills.insertCustom(d);

        List<Drill> found = drills.findAll();
        assertEquals(1, found.size());
        assertEquals("My Custom Drill", found.get(0).title);
        assertEquals("abc def ghi", found.get(0).body);
        assertEquals(2, found.get(0).tier);
    }

    @Test
    @DisplayName("Custom drill should reject empty title")
    void testRejectEmptyTitle() {
        Drill d = new Drill(0, "", "abc def", 1);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> drills.insertCustom(d));
        assertTrue(ex.getMessage().toLowerCase().contains("title"));
    }

    @Test
    @DisplayName("Custom drill should reject empty content")
    void testRejectEmptyContent() {
        Drill d = new Drill(0, "Valid Title", "   ", 1);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> drills.insertCustom(d));
        // Use plain ASCII double quotes here:
        assertTrue(ex.getMessage().toLowerCase().contains("content"));
    }

    @Test
    @DisplayName("Custom drill should store correct tier (e.g., 4)")
    void testInsertDrillTier() {
        Drill d = new Drill(0, "Tier 4 Drill", "xyz content", 4);
        drills.insertCustom(d);

        Drill found = drills.findAll().get(0);
        assertEquals(4, found.tier);
    }
}
