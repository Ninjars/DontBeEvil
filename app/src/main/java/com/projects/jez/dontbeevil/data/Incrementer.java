package com.projects.jez.dontbeevil.data;

import com.projects.jez.dontbeevil.state.IncrementerReadout;
import com.projects.jez.utils.observable.Observable;

/**
 * Created by Jez on 18/03/2016.
 */
public class Incrementer implements IncrementerReadout {
    private final String id;
    private final IncrementableValue value;

    public Incrementer(String id, IncrementableValue value) {
        this.id = id;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void increment() {
        value.increment();
    }

    @Override
    public Observable<Long> getValue() {
        return value.getValue();
    }

    @Override
    public String getTitle() {
        // TODO
        return id;
    }

    @Override
    public String getCaption() {
        // TODO
        return "insert caption here";
    }

    @Override
    public Integer getSortOrder() {
        // TODO
        return 0;
    }
}
