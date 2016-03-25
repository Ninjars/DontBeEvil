package com.projects.jez.dontbeevil.data;

import com.projects.jez.utils.observable.Observable;
import com.projects.jez.utils.observable.Source;

/**
 * Created by Jez on 18/03/2016.
 */
public class IncrementableValue {
    private final Source<Long> mCount = new Source<>(0l);
    private long mAmountPerTick = 1l;

    public Observable<Long> getValue() {
        return mCount.getObservable();
    }

    public void increment() {
        mCount.put(mCount.getObservable().getCurrent() + mAmountPerTick);
    }

    public void setAmountPerIncrement(long tickIncrement) {
        mAmountPerTick = tickIncrement;
    }
}
