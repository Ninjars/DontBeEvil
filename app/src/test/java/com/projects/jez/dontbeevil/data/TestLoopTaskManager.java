package com.projects.jez.dontbeevil.data;

import com.projects.jez.dontbeevil.engine.ILoopingTask;
import com.projects.jez.dontbeevil.engine.LoopTaskManager;

/**
 * Created by Jez on 12/10/2016.
 */

class TestLoopTaskManager extends LoopTaskManager {

    private Runnable lastTask;

    @Override
    public ILoopingTask startLoopingTask(String id, long period, Runnable task) {
        lastTask = task;
        return super.startLoopingTask(id, period, task);
    }

    public boolean runLastTask() {
        if (lastTask == null) {
            return false;
        }
        lastTask.run();
        return true;
    }

    @Override
    public void clear() {
        super.clear();
        lastTask = null;
    }
}
