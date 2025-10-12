package com.typinggame.config;

import com.typinggame.data.UserManager;

/**
 * Global app context (singleton) with backward compatibility for legacy static userManager access.
 */
public class AppContext {

    // ---- Singleton ----
    private static final AppContext INSTANCE = new AppContext();
    public static AppContext get() { return INSTANCE; }

    // ---- Back-compat: legacy static field ----
    /** @deprecated Use AppContext.get().getUserManager()/setUserManager() instead. */
    @Deprecated
    public static volatile UserManager userManager;  // kept ONLY for old code paths

    // ---- Preferred instance field ----
    private volatile UserManager instanceUserManager;

    public UserManager getUserManager() {
        return instanceUserManager;
    }

    public void setUserManager(UserManager um) {
        this.instanceUserManager = um;
        // keep legacy static in sync so old code continues working
        AppContext.userManager = um;
    }

    // ---- Drill / Level Context ----
    private Integer selectedTier;     // 1..10
    private Integer selectedDrillId;  // drill to start

    public Integer getSelectedTier() { return selectedTier; }
    public void setSelectedTier(Integer selectedTier) { this.selectedTier = selectedTier; }

    public Integer getSelectedDrillId() { return selectedDrillId; }
    public void setSelectedDrillId(Integer selectedDrillId) { this.selectedDrillId = selectedDrillId; }
}
