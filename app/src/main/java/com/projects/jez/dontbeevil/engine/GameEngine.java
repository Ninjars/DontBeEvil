package com.projects.jez.dontbeevil.engine;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by jez on 22/03/2016.
 */
public class GameEngine {

    private static final long sTargetLoopMillis = 1000 / 30; // 30 updates per second

    private final Handler cLoopHandler = new Handler(Looper.getMainLooper());

    private long mLastLoopStart;

    public void start() {
        cLoopHandler.post(new Runnable() {
            @Override
            public void run() {
                mLastLoopStart = System.nanoTime() / 1000000;
                onUpdateLoopStart();
            }
        });
    }

    private void onUpdateLoopStart() {
        long loopStart = System.nanoTime() / 1000000;
        long delta = loopStart - mLastLoopStart;
        mLastLoopStart = loopStart;
        update(delta);
    }

    private void update(long timeDelta) {
        final long loopStart = System.nanoTime();
    }

    private void onUpdateLoopEnd(long loopStart) {
        final long loopDuration = (System.nanoTime() - loopStart) / 1000;
        long delta = sTargetLoopMillis - loopDuration;
        if (delta < 0) delta = 0;
        cLoopHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onUpdateLoopStart();
            }
        }, delta);
    }
}
