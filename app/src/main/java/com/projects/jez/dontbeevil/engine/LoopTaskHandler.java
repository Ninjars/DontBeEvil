package com.projects.jez.dontbeevil.engine;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by jez on 22/03/2016.
 */
public class LoopTaskHandler {

    private final Handler cLoopHandler = new Handler(Looper.getMainLooper());
    private final Runnable mTask;

    private Long lastUpdateStarted;
    private Long lastUpdateStopped;
    private long mLoopPeriod;

    public LoopTaskHandler(final Runnable task, long loopTime) {
        mTask = new Runnable() {
            @Override
            public void run() {
                update();
                task.run();
            }
        };
        mLoopPeriod = loopTime;
    }

    public void start() {
        update();
    }

    private void update() {
        lastUpdateStarted = System.currentTimeMillis();
        cLoopHandler.postDelayed(mTask, mLoopPeriod);
    }

    public void stop() {
        lastUpdateStopped = System.currentTimeMillis();
        cLoopHandler.removeCallbacksAndMessages(null);
    }

    public void resume() {
        if (lastUpdateStarted == null || lastUpdateStopped == null) update();
        long delta = Math.min(Math.max(0, mLoopPeriod - (lastUpdateStopped - lastUpdateStarted)), mLoopPeriod);
        cLoopHandler.postDelayed(mTask, delta);
    }
}
