package com.projects.jez.dontbeevil.data;

import com.projects.jez.dontbeevil.content.IncrementerScript;
import com.projects.jez.dontbeevil.state.IncrementerReadout;
import com.projects.jez.utils.observable.Observable;

/**
 * Created by Jez on 18/03/2016.
 */
public class Incrementer implements IncrementerReadout {
    private final String id;
    private final IncrementableValue value = new IncrementableValue();
    private final IncrementerMetadata metadata;

    public Incrementer(IncrementerScript arg) {
        id = arg.getId();
        metadata = new IncrementerMetadata(arg.getMetadata());
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
        return metadata.getTitle();
    }

    @Override
    public String getCaption() {
        return metadata.getCaption();
    }

    @Override
    public Integer getSortOrder() {
        return metadata.getSortOrder();
    }
}
