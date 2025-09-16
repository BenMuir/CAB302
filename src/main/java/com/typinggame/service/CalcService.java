package com.typinggame.service;

public final class CalcService {
    private CalcService(){}

    public static double accuracy(String target, String typed){
        if(target == null || target.isEmpty()) return 0.0;
        int n = target.length();
        int m = typed == null ? 0 : typed.length();
        int L = Math.min(n, m);
        int correct = 0;
        for(int i=0;i<L;i++){
            if(target.charAt(i) == typed.charAt(i)) correct++;
        }
        int over = Math.max(0, m - n);
        int score = Math.max(0, correct - over);
        return 100.0 * score / n;
    }

    public static double wpm(int typedChars, double elapsedSeconds){
        if(elapsedSeconds <= 0) return 0.0;
        double words = typedChars / 5.0;
        return words / (elapsedSeconds / 60.0);
    }
}
