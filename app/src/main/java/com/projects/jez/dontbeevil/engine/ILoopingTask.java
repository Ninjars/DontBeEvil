package com.projects.jez.dontbeevil.engine;

import android.support.annotation.Nullable;

/**
 * Created by jez on 26/03/2016.
 */
public interface ILoopingTask {

    @Nullable
    Range getRange();
    void setPeriod(long period);
}
