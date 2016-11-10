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

    /**
     * Start a new looping task.  Will throw if the task is already running as a sanity check
     *
     * @param id new task id
     * @param period period of update in milliseconds
     * @param task task to perform on every loop
     * @return started handler interface
     */
    public ILoopingTask startLoopingTask(String id, long period, Runnable task) {
        if (mTaskHandlers.containsKey(id)) {
            throw new DuplicateLoopTaskRuntimeError(id);
        }
        LoopTaskHandler handler = new LoopTaskHandler(task, period);
        mTaskHandlers.put(id, handler);
        handler.start();
        return handler;
    }

    /**
     * Start a new looping task.  Won't throw if task is already running; instead it will
     * stop the existing task and start this one instead. Useful for ui rebinding.
     *
     * @param id new task id
     * @param period period of update in milliseconds
     * @param task task to perform on every loop
     * @return started handler interface
     */
    public ILoopingTask startOrReplaceLoopingTask(String id, int period, Runnable task) {
        stopLoopingTask(id);
        return startLoopingTask(id, period, task);
    }

    public void updatePeriod(String id, long newPeriod) {
        LoopTaskHandler handler = mTaskHandlers.get(id);
        if (handler == null) {
            throw new UnknownLoopTaskRuntimeError(id);
        }
        handler.setPeriod(newPeriod);
    }

    /**
     * stops all handlers
     */
    public void pauseAll() {
        for (LoopTaskHandler handler : mTaskHandlers.values()) {
            handler.stop();
        }
    }

    /**
     * resumes all handlers
     */
    public void resumeAll() {
        for (LoopTaskHandler handler : mTaskHandlers.values()) {
            handler.resume();
        }
    }

    public boolean has(String id) {
        return mTaskHandlers.containsKey(id);
    }

    /**
     * Stop a task.  Will not complain if no task by that id can be found.
     * @param id id of task to stop
     */
    public void stopLoopingTask(String id) {
        LoopTaskHandler handler = mTaskHandlers.get(id);
        if (handler != null) {
            handler.stop();
            mTaskHandlers.remove(id);
        }
    }

    /**
     * stops and removes references to all tasks
     */
    public void clear() {
        pauseAll();
        mTaskHandlers.clear();
    }
}
