package com.typinggame.config;

public final class Config {
    private Config(){}
    public static final double UNLOCK_SCORE_THRESHOLD = 2500.0; // e.g., 50 WPM * 50% acc
    public static final int SESSIONS_TO_UNLOCK_NEXT = 2;        // plays in current tier
}
