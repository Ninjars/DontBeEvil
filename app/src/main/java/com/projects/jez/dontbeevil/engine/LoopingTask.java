package com.projects.jez.dontbeevil.engine;

import com.projects.jez.utils.Box;
import com.projects.jez.utils.observable.Observable;

/**
 * Created by jez on 26/03/2016.
 */
public interface LoopingTask {
    Observable<Box<Range>> getRangeObservable();
    void setPeriod(long period);
}
