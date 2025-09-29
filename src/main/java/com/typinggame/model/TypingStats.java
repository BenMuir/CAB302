package com.typinggame.model;

/**
 * Tracks typing performance metrics during a session, including accuracy,
 * words per minute (WPM), and streaks of correct input. This class is updated
 * in real time as the user types and is used to evaluate session results.
 *
 * @author Ben M
 * @since 2025-08-16
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

    /**
     * Constructs a new TypingStats object for the given target sentence.
     *
     * @param target The sentence the user is expected to type
     */
    public TypingStats(String target) {
        this.target = target;
    }

    /**
     * Updates the current input string.
     *
     * @param input The latest input typed by the user
     */
    public void update(String input) {
        this.currentInput = input;
    }

    /**
     * Checks if the user has completed typing the full target sentence.
     *
     * @return true if the input matches the target sentence; false otherwise
     */
    public boolean isComplete() {
        return currentInput.equals(target);
    }

    /**
     * Calculates the user's typing speed in words per minute (WPM).
     * Assumes one word is defined as a whitespace-separated token.
     *
     * @param elapsedMinutes Time elapsed since the start of the session, in minutes
     * @return Estimated words per minute
     */
    public int calculateWPM(double elapsedMinutes) {
        if (elapsedMinutes <= 0) return 0;
        int wordCount = target.split("\\s+").length;
        return (int) (wordCount / elapsedMinutes);
    }

    /**
     * Updates accuracy statistics based on the current input.
     * Compares typed characters to the target and tracks new errors.
     *
     * @param userInput  Current input from the user
     * @param targetText Target sentence to compare against
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
     * Returns the current typing accuracy as a percentage.
     *
     * @return Accuracy percentage (0–100)
     */
    public double getAccuracy() {
        int totalAttempts = correctChars + cumulativeErrors;
        if (totalAttempts == 0) return 100.0;
        return (correctChars * 100.0) / totalAttempts;
    }

    /**
     * Updates the streak count based on the latest character typed.
     * Resets streak on mistake and updates best streak if needed.
     *
     * @param inputChar  Character typed by the user
     * @param targetChar Expected character from the target sentence
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

    /**
     * Returns the current streak of consecutive correct characters typed.
     *
     * @return Current streak count
     */
    public int getCurrentStreak() {
        return currentStreak;
    }

    /**
     * Returns the best streak achieved during the session.
     *
     * @return Best streak count
     */
    public int getBestStreak() {
        return bestStreak;
    }
}