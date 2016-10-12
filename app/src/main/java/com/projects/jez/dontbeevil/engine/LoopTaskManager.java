package com.projects.jez.dontbeevil.engine;

import com.projects.jez.dontbeevil.errors.DuplicateLoopTaskRuntimeError;
import com.projects.jez.dontbeevil.errors.UnknownLoopTaskRuntimeError;

import java.util.HashMap;

/**
 * Created by Jez on 25/03/2016.
 * LoopTaskManager is intended to be the central location that manages LoopTaskHandler state,
 * pausing, resuming and updating all LoopTaskHandlers, without exposing them to the rest of the app
 */
public class LoopTaskManager {

    private final HashMap<String, LoopTaskHandler> mTaskHandlers = new HashMap<>();

    public ILoopingTask startLoopingTask(String id, long period, Runnable task) {
        if (mTaskHandlers.containsKey(id)) {
            throw new DuplicateLoopTaskRuntimeError(id);
        }
        LoopTaskHandler handler = new LoopTaskHandler(task, period);
        mTaskHandlers.put(id, handler);
        handler.start();
        return handler;
    }

    public void updatePeriod(String id, long newPeriod) {
        LoopTaskHandler handler = mTaskHandlers.get(id);
        if (handler == null) {
            throw new UnknownLoopTaskRuntimeError(id);
        }
        handler.setPeriod(newPeriod);
    }

    public void pauseAll() {
        for (LoopTaskHandler handler : mTaskHandlers.values()) {
            handler.stop();
        }
    }

    public void resumeAll() {
        for (LoopTaskHandler handler : mTaskHandlers.values()) {
            handler.resume();
        }
    }

    public boolean has(String id) {
        return mTaskHandlers.containsKey(id);
    }

    public void stopLoopingTask(String id) {
        LoopTaskHandler handler = mTaskHandlers.get(id);
        if (handler != null) {
            handler.stop();
            mTaskHandlers.remove(id);
        }
    }

    public void clear() {
        pauseAll();
        mTaskHandlers.clear();
    }
}
