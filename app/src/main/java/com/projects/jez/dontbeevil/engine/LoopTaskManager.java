package com.projects.jez.dontbeevil.engine;

import com.projects.jez.dontbeevil.errors.DuplicateLoopTaskRuntimeError;
import com.projects.jez.dontbeevil.errors.UnknownLoopTaskRuntimeError;
import com.projects.jez.utils.Box;
import com.projects.jez.utils.observable.Observable;

import java.util.HashMap;

/**
 * Created by Jez on 25/03/2016.
 */
public class LoopTaskManager {

    private final HashMap<String, LoopTaskHandler> mTaskHandlers = new HashMap<>();

    public Observable<Box<Range>> startLoopingTask(String id, Runnable task, long period) {
        if (mTaskHandlers.containsKey(id)) {
            throw new DuplicateLoopTaskRuntimeError(id);
        }
        LoopTaskHandler handler = new LoopTaskHandler(task, period);
        mTaskHandlers.put(id, handler);
        handler.start();
        return handler.getRangeObservable();
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
}
