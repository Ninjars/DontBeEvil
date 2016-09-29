package com.projects.jez.dontbeevil.engine;

/**
 * Created by Jez on 25/03/2016.
 * Defines a value range, eg the duration of an update loop.
 * Can be queried to provide a progression.
 */
class EmptyRange extends Range {

    private static Range instance = new EmptyRange();

    static Range getInstance() {
        return instance;
    }

    @Override
    public double getCappedProgression(long currentValue) {
        return 0;
    }

    @Override
    void update(long start, long range) {
       // Nothing
    }
}
