package com.projects.jez.utils;

import android.os.Handler;
import android.os.Looper;

import com.projects.jez.utils.observable.Observable;
import com.projects.jez.utils.observable.Observer;
import com.projects.jez.utils.observable.Source;

/**
 * Created by jez on 26/03/2016.
 * Singleton intended to provide update signal to UI elements that need to poll rather than
 * being updated by Observables or interactions.
 * Can be stopped and resumed, and is running by default.
 */
public class RefreshSignalProvider {
    private static final long sRefreshMillis = 1000 / 30;
    private static RefreshSignalProvider sInstance;

    private final Handler mRefreshHandler = new Handler(Looper.getMainLooper());
    private final Source<Boolean> mRefreshSource = new Source<>(false);

    public static RefreshSignalProvider getInstance() {
        if (sInstance == null) {
            sInstance = new RefreshSignalProvider();
        }
        return sInstance;
    }

    protected RefreshSignalProvider() {
        // protected constructor
        mRefreshSource.getObservable().addObserverImmediate(new Observer<Boolean>() {
            @Override
            public void observe(Boolean arg) {
                mRefreshHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshSource.put(true);
                    }
                }, sRefreshMillis);
            }
        });
    }

    public void start() {
        mRefreshSource.put(true);
    }

    public void pause() {
        mRefreshHandler.removeCallbacksAndMessages(null);
    }

    public Observable<Boolean> getSignal() {
        return mRefreshSource.getObservable();
    }
}
