package com.projects.jez.dontbeevil.engine;

import com.projects.jez.dontbeevil.BuildConfig;

/**
 * Created by Jez on 25/03/2016.
 * Defines a value range, eg the duration of an update loop.
 * Can be queried to provide a progression.
 */
public class Range {

    private final long mStart;
    private final long mRange;

    public Range(long start, long range) {
        mStart = start;
        mRange = range;
        if (BuildConfig.DEBUG && mRange > 0) throw new RuntimeException("cannot have negative range: value was " + range);
    }

    public double getCappedProgression(long currentValue) {
        return Math.max(0, Math.min(1.0, currentValue - mStart / mRange));
    }
}
