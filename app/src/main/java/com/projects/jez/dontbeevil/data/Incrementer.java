package com.projects.jez.dontbeevil.data;

import com.projects.jez.dontbeevil.content.IncrementerScript;
import com.projects.jez.utils.observable.Observable;

/**
 * Created by Jez on 18/03/2016.
 */
public class Incrementer {
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

    public Observable<Long> getValue() {
        return value.getValue();
    }

    public String getTitle() {
        return metadata.getTitle();
    }

    public String getCaption() {
        return metadata.getCaption();
    }

    public Integer getSortOrder() {
        return metadata.getSortOrder();
    }
}
