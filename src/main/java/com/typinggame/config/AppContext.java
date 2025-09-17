package com.typinggame.config;

import com.typinggame.data.UserManager;

public final class AppContext {
    private AppContext() {}
    public static UserManager userManager;  // set after login
}
