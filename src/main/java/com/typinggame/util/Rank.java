package com.typinggame.util;

public enum Rank {
    KRILL("/images/Badges/Krill.png"),
    CLOWNFISH("/images/Badges/ClownFish.png"),
    TUNA("/images/Badges/Tuna.png"),
    SWORDFISH("/images/Badges/SwordFish.png"),
    WHALE("/images/Badges/Whale.png");

    public final String resourcePath;

    Rank(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public static Rank forTypingSpeed(double WPM) {
        if (WPM >= 120) return WHALE;
        if (WPM >= 90)  return SWORDFISH;
        if (WPM >= 60)  return TUNA;
        if (WPM >= 30)  return CLOWNFISH;
        return KRILL;
    }
}