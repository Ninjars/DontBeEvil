package com.projects.jez.dontbeevil.managers;

import com.projects.jez.dontbeevil.engine.LoopTaskManager;

/**
 * Created by Jez on 18/03/2016.
 */
public final class Environment {
    private static Environment instance = null;

    private final IncrementerManager incrementerManager;
    private final GameManager gameManager;
    private final LoopTaskManager taskManager;

    private Environment() {
        incrementerManager = new IncrementerManager();
        gameManager = new GameManager(incrementerManager);
        taskManager = new LoopTaskManager();
    }

    public static synchronized Environment getInstance() {
        if (instance == null) {
            instance = new Environment();
        }
        return instance;
    }

    public IncrementerManager getIncrementerManager() {
        return incrementerManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public LoopTaskManager getTaskManager() {
        return taskManager;
    }
}
