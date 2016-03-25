package com.projects.jez.dontbeevil.engine;

import android.os.Handler;
import android.os.Looper;

import com.projects.jez.utils.Box;
import com.projects.jez.utils.observable.Observable;
import com.projects.jez.utils.observable.Source;

/**
 * Created by jez on 22/03/2016.
 */
public class LoopTaskHandler {

    private final Handler cLoopHandler = new Handler(Looper.getMainLooper());
    private final Source<Box<Range>> mRange = new Source<>(new Box<Range>(null));
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
        lastUpdateStopped = null;
        update();
    }

    private void update() {
        lastUpdateStarted = System.currentTimeMillis();
        mRange.put(new Box<>(new Range(lastUpdateStarted, mLoopPeriod)));
        cLoopHandler.postDelayed(mTask, mLoopPeriod);
    }

    public void stop() {
        lastUpdateStopped = System.currentTimeMillis();
        mRange.put(new Box<Range>(null));
        cLoopHandler.removeCallbacksAndMessages(null);
    }

    public void resume() {
        if (lastUpdateStopped != null) {
            // does not appear to have been stopped, ignore resume
            //noinspection UnnecessaryReturnStatement
            return;

        } else if (lastUpdateStarted == null) {
            // does not appear to have been started, so start
            start();

        } else {
            // resuming accounting for previous progress
            lastUpdateStopped = null;
            long delta = Math.min(Math.max(0, mLoopPeriod - (lastUpdateStopped - lastUpdateStarted)), mLoopPeriod);
            mRange.put(new Box<>(new Range(System.currentTimeMillis() - (mLoopPeriod - delta), mLoopPeriod)));
            cLoopHandler.postDelayed(mTask, delta);
        }
    }

    public void setPeriod(long period) {
        mLoopPeriod = period;
        stop();
        resume();
    }

    public Observable<Box<Range>> getRangeObservable() {
        return mRange.getObservable();
    }
}
