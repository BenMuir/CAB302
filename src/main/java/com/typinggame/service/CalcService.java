package com.typinggame.service;

/**
 * Basic metrics calculations (accuracy and WPM).
 * Static utility; no state.
 */
public final class CalcService {
    private CalcService(){}

    /**
     * Percentage of correct characters vs target length.
     * - Compares positions up to the shorter length.
     * - Penalises extra typed chars beyond target.
     * - Returns 0..100.
     */
    public static double accuracy(String target, String typed){
        if (target == null || target.isEmpty()) return 0.0;
        int n = target.length();
        int m = (typed == null) ? 0 : typed.length();

        int L = Math.min(n, m);
        int correct = 0;
        for (int i = 0; i < L; i++) {
            if (target.charAt(i) == typed.charAt(i)) correct++;
        }

        int over = Math.max(0, m - n);           // over-typed chars
        int score = Math.max(0, correct - over); // don’t go below 0
        return 100.0 * score / n;
    }

    /**
     * Words per minute using 5 chars = 1 word.
     * - Returns 0 if elapsedSeconds <= 0.
     */
    public static double wpm(int typedChars, double elapsedSeconds){
        if (elapsedSeconds <= 0) return 0.0;
        double words = typedChars / 5.0;
        return words / (elapsedSeconds / 60.0);
    }
}
