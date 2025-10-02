package com.typinggame.model;

/**
 * TypingStats is a model class that tracks the user's typing performance.
 * It handles accuracy, WPM calculation, and streak tracking for correct input.
 * This class is updated in real time as the user types.
 * [Ben M - Aug 16 2025]
 */
public class TypingStats {

    // Input tracking fields
    private String previousInput = "";       // Stores last input for error comparison
    private int totalTypedChars = 0;         // Total characters typed so far
    private int correctChars = 0;            // Number of correct characters typed
    private int cumulativeErrors = 0;        // Total number of mistakes made

    // Streak tracking fields
    private int currentStreak = 0;           // Current streak of correct characters
    private int bestStreak = 0;              // Highest streak achieved during session

    // Target sentence and current input
    private final String target;             // Sentence the user is trying to type
    private String currentInput = "";        // Latest input from the user

    // Constructor initializes with the target sentence
    public TypingStats(String target) {
        this.target = target;
    }

    // Updates the current input string
    public void update(String input) {
        this.currentInput = input;
    }

    // Checks if the user has completed typing the full target sentence
    public boolean isComplete() {
        return currentInput.equals(target);
    }

    /**
     * Calculates words per minute (WPM).
     * Assumes one word = one whitespace-separated token.
     *
     * @param elapsedMinutes Time elapsed in minutes
     * @return Estimated WPM
     *
     * [Ben M - Aug 16 2025]
     */
    public int calculateWPM(double elapsedMinutes) {
        if (elapsedMinutes <= 0) return 0;
        int wordCount = target.split("\\s+").length;
        return (int) (wordCount / elapsedMinutes);
    }

    /**
     * Updates accuracy stats based on current input.
     * Compares typed characters to target and tracks new errors.
     *
     * @param userInput   Current input from user
     * @param targetText  Target sentence to compare against
     *
     *[Ben M - Aug 16 2025]
     */
    public void updateAccuracy(String userInput, String targetText) {
        totalTypedChars = userInput.length();
        correctChars = 0;

        for (int i = 0; i < Math.min(userInput.length(), targetText.length()); i++) {
            char typed = userInput.charAt(i);
            char expected = targetText.charAt(i);

            if (typed == expected) {
                correctChars++;
            } else {
                // Only count new mistakes (not previously typed errors)
                if (i >= previousInput.length() || previousInput.charAt(i) == expected) {
                    cumulativeErrors++;
                }
            }
        }

        previousInput = userInput; // Store input for next comparison
    }

    /**
     * Returns current accuracy as a percentage.
     *
     *
     * @return Accuracy percentage
     *
     * [Ben M - Aug 16 2025]
     */
    public double getAccuracy() {
        int totalAttempts = correctChars + cumulativeErrors;
        if (totalAttempts == 0) return 100.0;
        return (correctChars * 100.0) / totalAttempts;
    }

    /**
     * Updates streak count based on latest character typed.
     * Resets streak on mistake, updates best streak if needed.
     *
     * @param inputChar  Character typed by user
     * @param targetChar Expected character from target sentence
     *
     * [Ben M - Aug 16 2025]
     */
    public void updateStreak(char inputChar, char targetChar) {
        if (inputChar == targetChar) {
            currentStreak++;
            if (currentStreak > bestStreak) {
                bestStreak = currentStreak;
            }
        } else {
            currentStreak = 0; // Reset streak on mistake
        }
    }

    // Returns current streak count
    public int getCurrentStreak() {
        return currentStreak;
    }

    // Returns best streak achieved during session
    public int getBestStreak() {
        return bestStreak;
    }
}