package com.projects.jez.utils.observable;

public interface Filter<T> {
    boolean passes(T arg);
}
