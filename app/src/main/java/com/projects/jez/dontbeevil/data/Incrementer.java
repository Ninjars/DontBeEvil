package com.projects.jez.dontbeevil.data;

/**
 * Created by Jez on 18/03/2016.
 */
public class Incrementer {
    private final String id;
    private final IncrementableValue value;

    public Incrementer(String id, IncrementableValue value) {
        this.id = id;
        this.value = value;
    }

    public IncrementableValue getValue() {
        return value;
    }

    public String getId() {
        return id;
    }
}
