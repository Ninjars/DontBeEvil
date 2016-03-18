package com.projects.jez.dontbeevil;

import com.projects.jez.dontbeevil.managers.IncrementerManager;

/**
 * Created by Jez on 18/03/2016.
 */
public final class Environment {
    private static Environment instance = null;

    private final IncrementerManager mIncrementerManager;

    private Environment() {
        mIncrementerManager = new IncrementerManager();
    }

    public static synchronized Environment getInstance() {
        if (instance == null) {
            instance = new Environment();
        }
        return instance;
    }

    public IncrementerManager getIncrementerManager() {
        return mIncrementerManager;
    }
}
