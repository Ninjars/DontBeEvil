package com.projects.jez.dontbeevil;

import com.projects.jez.dontbeevil.data.Incrementer;
import com.projects.jez.dontbeevil.managers.Environment;
import com.projects.jez.dontbeevil.ui.fragments.MainActivityFragment;
import com.projects.jez.utils.Logger;

/**
 * Created by Jez on 05/10/2016.
 */

public class DebugConfig {
    public static final boolean DEBUG_ALLOW_INVALID_PURCHASE_ACTIONS = true;
    public static final boolean ENABLE_DEBUG_LOGGING = true;

    // Add or remove classes from this in order to have them log.
    private static final Class[] ACTIVE_LOGGERS = {
            Incrementer.class,
            Environment.class,
            MainActivityFragment.class
    };

    static void init() {
        if (ENABLE_DEBUG_LOGGING) {
            Logger.registerAll(ACTIVE_LOGGERS);
        }
    }
}
