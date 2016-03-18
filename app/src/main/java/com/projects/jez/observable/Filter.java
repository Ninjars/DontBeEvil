package com.projects.jez.observable;

public interface Filter<T> {
    boolean passes(T arg);
}
