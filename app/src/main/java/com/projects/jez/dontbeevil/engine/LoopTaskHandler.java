package com.projects.jez.dontbeevil.engine;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by jez on 22/03/2016.
 */
public class LoopTaskHandler implements ILoopingTask {

    private final Handler cLoopHandler = new Handler(Looper.getMainLooper());
    private final Runnable mTask;
    private Range mRange;
    private Long lastUpdateStarted;
    private Long lastUpdateStopped;
    private long mLoopPeriod;

    public LoopTaskHandler(final Runnable task, long loopTime) {
        mTask = new Runnable() {
            @Override
            public void run() {
                task.run();
                update();
            }
        };
        mLoopPeriod = loopTime;
    }

    void start() {
        lastUpdateStopped = null;
        mRange = new Range();
        cLoopHandler.post(new Runnable() {
            @Override
            public void run() {
                update();
            }
        });
    }

    private void update() {
        lastUpdateStarted = System.currentTimeMillis();
        mRange.update(lastUpdateStarted, mLoopPeriod);
        cLoopHandler.postDelayed(mTask, mLoopPeriod);
    }

    void stop() {
        lastUpdateStopped = System.currentTimeMillis();
        mRange = null;
        cLoopHandler.removeCallbacksAndMessages(null);
    }

    void resume() {
        if (lastUpdateStopped == null) {
            // does not appear to have been stopped, ignore resume
            //noinspection UnnecessaryReturnStatement
            return;

        } else if (lastUpdateStarted == null) {
            // does not appear to have been started, so start
            start();

        } else {
            // resuming accounting for previous progress
            long delta = Math.min(Math.max(0, mLoopPeriod - (lastUpdateStopped - lastUpdateStarted)), mLoopPeriod);
            lastUpdateStopped = null;
            mRange = new Range();
            mRange.update(System.currentTimeMillis() - (mLoopPeriod - delta), mLoopPeriod);
            cLoopHandler.postDelayed(mTask, delta);
        }
    }

    @Override
    public void setPeriod(long period) {
        mLoopPeriod = period;
        stop();
        resume();
    }

    @Override
    public Range getRange() {
        return mRange;
    }
}
