package com.typinggame.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TypingStats class.
 * Validates accuracy, WPM, streak tracking, and completion logic.
 * <p>
 * [Ben M – Sep 8 2025]
 */
public class TypingStatsTest {

    @Test
    public void testAccuracyCalculation() {
        TypingStats stats = new TypingStats("hello world");
        stats.updateAccuracy("hello worxd", "hello world"); // 1 mistake
        double accuracy = stats.getAccuracy();
        assertEquals(90.91, accuracy, 0.01); // 10/11 correct
    }

    @Test
    public void testWPMCalculation() {
        TypingStats stats = new TypingStats("hello world");
        int wpm = stats.calculateWPM(1.0); // 1 minute
        assertEquals(2, wpm); // "hello world" = 2 words
    }

    @Test
    public void testStreakTracking() {
        TypingStats stats = new TypingStats("abc");
        stats.updateStreak('a', 'a');
        stats.updateStreak('b', 'b');
        stats.updateStreak('x', 'c'); // mistake
        assertEquals(0, stats.getCurrentStreak());
        assertEquals(2, stats.getBestStreak());
    }

    @Test
    public void testCompletionCheck() {
        TypingStats stats = new TypingStats("done");
        stats.update("done");
        assertTrue(stats.isComplete());
    }

    @Test
    public void testPerfectAccuracy() {
        TypingStats stats = new TypingStats("hello");
        stats.updateAccuracy("hello", "hello");
        assertEquals(100.0, stats.getAccuracy());
    }

    @Test
    public void testIncompleteInput() {
        TypingStats stats = new TypingStats("hello");
        stats.update("hel");
        assertFalse(stats.isComplete());
    }

    @Test
    public void testStreakResets() {
        TypingStats stats = new TypingStats("abc");
        stats.updateStreak('a', 'a');
        stats.updateStreak('b', 'b');
        stats.updateStreak('x', 'c'); // incorrect
        stats.updateStreak('c', 'c'); // correct again
        assertEquals(1, stats.getCurrentStreak());
        assertEquals(2, stats.getBestStreak());
    }

    @Test
    public void testWPMZeroTime() {
        TypingStats stats = new TypingStats("hello world");
        int wpm = stats.calculateWPM(0.0);
        assertEquals(0, wpm);
    }

    @Test
    public void testInputShorterThanTarget() {
        TypingStats stats = new TypingStats("hello world");
        stats.updateAccuracy("hello", "hello world"); // 5 correct, no penalty
        double accuracy = stats.getAccuracy();
        assertEquals(100.0, accuracy, 0.01);
    }

    @Test
    public void testEmptyInputAccuracy() {
        TypingStats stats = new TypingStats("hello");
        stats.updateAccuracy("", "hello");
        double accuracy = stats.getAccuracy();
        assertEquals(100.0, accuracy, 0.01); // No attempts = 100% by current logic
    }

    @Test
    public void testTrailingWhitespaceIgnored() {
        TypingStats stats = new TypingStats("hello");
        stats.updateAccuracy("hello ", "hello"); // trailing space
        double accuracy = stats.getAccuracy();
        assertEquals(100.0, accuracy, 0.01); // Matches current logic
    }

    @Test
    public void testMultipleMistakesTracked() {
        TypingStats stats = new TypingStats("abcdef");
        stats.updateAccuracy("abxdxf", "abcdef"); // mistakes at index 2 and 4
        double accuracy = stats.getAccuracy();
        assertEquals(66.67, accuracy, 0.01); // 4/6 correct
    }
}