package com.projects.jez.dontbeevil.managers;

import android.content.Context;

import com.projects.jez.dontbeevil.content.ContentLoader;
import com.projects.jez.dontbeevil.engine.LoopTaskManager;

/**
 * Created by Jez on 18/03/2016.
 */
public final class Environment {
    private static Environment instance = null;

    private final IncrementerManager incrementerManager;
    private final LoopTaskManager taskManager;
    private final ContentLoader contentLoader;

    private Environment(Context context) {
        contentLoader = new ContentLoader(context);
        incrementerManager = new IncrementerManager();
        taskManager = new LoopTaskManager();
    }

    public static synchronized Environment getInstance(Context context) {
        if (instance == null) {
            instance = new Environment(context);
        }
        return instance;
    }

    public IncrementerManager getIncrementerManager() {
        return incrementerManager;
    }

    public LoopTaskManager getTaskManager() {
        return taskManager;
    }

    public ContentLoader getContentLoader() {
        return contentLoader;
    }
}
